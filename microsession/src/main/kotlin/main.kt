import config.ConfigLoader
import utils.calculatePort

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroSessionBootstrap.init(config.Services.SESSION.calculatePort(args))
}