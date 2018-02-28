import config.ConfigLoader
import config.Services

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    MicroDatabaseBootstrap.init(Services.isNotStartedIndependently())
}