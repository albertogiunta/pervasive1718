package utils

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import spark.Spark
import java.util.concurrent.atomic.AtomicBoolean

class JdbiConfiguration private constructor() {

    val jdbi = Jdbi
        .create("$DB_URL$DB_NAME", USERNAME, PASSWORD)
        .installPlugin(PostgresPlugin())
        .installPlugin(SqlObjectPlugin())
        .installPlugin(KotlinPlugin())
        .installPlugin(KotlinSqlObjectPlugin())!!

    init {
        Spark.port(8080)
    }

    companion object {
        const val DB_URL = "jdbc:postgresql://2.234.121.101:5432/"
        const val DB_NAME = "tiopentone"
        const val USERNAME = "pervasive"
        const val PASSWORD = "zeronegativo"

        lateinit var INSTANCE: JdbiConfiguration
        private val initialized = AtomicBoolean()

        fun init() {
            if (!initialized.getAndSet(true)) {
                INSTANCE = JdbiConfiguration()
            }
        }

    }
}