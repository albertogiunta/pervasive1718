import config.ConfigLoader
import utils.KaazingGatewayStarter
import utils.killKaazingGatewayOnFatherDeath

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    println("MicroSession microservice booting up...")
    println("Kaazing Gateway Starting...")
    KaazingGatewayStarter.startGateway().killKaazingGatewayOnFatherDeath()
    MicroSessionBootstrap.init()
}