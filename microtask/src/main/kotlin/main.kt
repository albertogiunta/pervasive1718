import config.ConfigLoader
import config.Services
import utils.calculatePort

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroTaskBootstrap.init(Services.TASK_HANDLER.calculatePort(args))
}
