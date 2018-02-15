import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroDatabaseBootstrap.init(Connection.DB_PORT)
}