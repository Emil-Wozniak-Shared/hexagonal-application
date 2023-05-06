package pl.ejdev.postgres.infrastructure.core.utils

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PGobject
import pl.ejdev.error.BaseError
import pl.ejdev.error.InternalError
import java.sql.ResultSet

suspend fun <T> query(block: suspend Transaction.() -> T): Either<BaseError, T> =
    either<Exception, T> {
        newSuspendedTransaction(Dispatchers.IO) {
            addLogger(StdOutSqlLogger)
            block()
        }
    }.mapLeft(::InternalError)

fun <T> rightIsNotNull(either: T?): Boolean = either.right().getOrNull() != null

inline fun <T> Either<BaseError, T?>.mapNotNull(
    condition: (T?) -> Boolean,
    raise: () -> BaseError
): Either<BaseError, T> =
    this.flatMap {
        either {
            ensure(condition(it), raise)
            it!!
        }
    }

@Suppress("UNCHECKED_CAST")
fun <T, R> ResultSet.set(name: String, transform: (it: T) -> R) =
    (this.getArray(name).array as Array<T>).map(transform)
        .toSet()

fun Table.array(name: String, columnType: IColumnType): Column<Any> =
    registerColumn(name, ArrayColumnType(columnType))

class ArrayColumnType(private val type: IColumnType) : ColumnType() {
    private fun supportsArrays() =
        true // !loritta.config.database.type.startsWith("SQLite")

    override fun sqlType(): String =
        buildString {
            when {
                !supportsArrays() -> append("TEXT")
                else -> {
                    append(type.sqlType())
                    append(" ARRAY")
                }
            }
        }

    override fun valueToDB(value: Any?): Any? =
        when {
            !supportsArrays() -> "'NOT SUPPORTED'"
            value is Array<*> -> {
                val columnType = type.sqlType()
                    .split("(")[0]
                val jdbcConnection = (TransactionManager.current().connection as JdbcConnectionImpl).connection
                jdbcConnection.createArrayOf(columnType, value)
            }

            else -> super.valueToDB(value)
        }

    override fun valueFromDB(value: Any): Any {
        if (!supportsArrays()) {
            val clazzName = type::class.simpleName
            if (clazzName == "LongColumnType") return arrayOf<Long>()
            if (clazzName == "TextColumnType") return arrayOf<String>()
            error("Unsupported Column Type")
        }

        if (value is java.sql.Array) {
            return value.array
        }
        if (value is Array<*>) {
            return value
        }
        error("Array does not support for this database")
    }

    override fun notNullValueToDB(value: Any): Any {
        if (!supportsArrays()) return "'NOT SUPPORTED'"

        if (value is Array<*>) {
            if (value.isEmpty()) return "'{}'"

            val columnType = type.sqlType()
                .split("(")[0]
            val jdbcConnection = (TransactionManager.current().connection as JdbcConnectionImpl).connection
            return jdbcConnection.createArrayOf(columnType, value) ?: error("Can't create non null array for $value")
        } else {
            return super.notNullValueToDB(value)
        }
    }
}

fun <T : Any> Table.jsonb(name: String, jsonParser: IJsonParser, nullable: Boolean): Column<T> =
    registerColumn<T>(name, ParsedJsonColumnType<T>(jsonParser, nullable))

class ParsedJsonColumnType<out T : Any>(
    private val parser: IJsonParser,
    override var nullable: Boolean = false,
) : IColumnType {
    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val obj = PGobject()
        obj.type = "jsonb"
        value?.let { obj.value = parser.toJson(it) }
        stmt[index] = obj
    }

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    override fun valueFromDB(value: Any): Any =
        when (value) {
            is HashMap<*, *> -> value
            is Map<*, *> -> value
            else -> {
                value as PGobject
                try {
                    val json = value.value!!
                    parser.fromJson(json)!! // this will throw null reference on purpose
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw RuntimeException("Can't parse JSON: $value")
                }
            }
        }

    override fun notNullValueToDB(value: Any): Any =
        parser.toJson(value)

    override fun nonNullValueToString(value: Any): String =
        "'${parser.toJson(value)}'"

    override fun sqlType() =
        "jsonb"
}

// Interface
interface IJsonParser {
    fun fromJson(json: String): Any?
    fun toJson(source: Any): String
}

// Implementation of jsonb field with parser as 'MetaData' is abstract class with Type embedded
//val classTypeMetadata = jsonb<MetaData>("meta_data", MetaDataParser(), true)
