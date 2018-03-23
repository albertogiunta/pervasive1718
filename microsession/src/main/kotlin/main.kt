import config.ConfigLoader
import utils.KaazingGatewayStarter

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    println("MicroSession microservice booting up...")
    println("Kaazing Gateway Starting...")
    KaazingGatewayStarter.startGateway()
    MicroSessionBootstrap.init()
}