import config.ConfigLoader
import utils.KaazingGatewayStarter
import utils.killOnFatherDeath

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    println("MicroSession microservice booting up...")
    println("Kaazing Gateway Starting...")
    KaazingGatewayStarter.startGateway().killOnFatherDeath()
    MicroSessionBootstrap.init()
}