import config.ConfigLoader
import config.Services

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroDatabaseBootstrap.init(Services.DATA_BASE.port)
}