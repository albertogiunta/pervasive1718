import config.ConfigLoader
import config.Services
import utils.calculatePort

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroDatabaseBootstrap.init(Services.DATA_BASE.calculatePort(args))
}