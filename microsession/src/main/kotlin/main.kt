import config.ConfigLoader
import utils.KaazingGatewayUtils
import utils.killOnFatherDeath

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    println("MicroSession microservice booting up...")
    println("Kaazing Gateway Starting...")
    KaazingGatewayUtils.startGateway().killOnFatherDeath(KaazingGatewayUtils.onProcessFatherDeath)
    MicroSessionBootstrap.init()
}