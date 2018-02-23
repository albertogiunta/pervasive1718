import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    println("MicroSession microservice booting up...")
    MicroSessionBootstrap.init()
    waitInitAndNotifiyToMicroSession(args[0].toInt())
}