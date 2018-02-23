import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    MicroDatabaseBootstrap.init()
    waitInitAndNotifiyToMicroSession(args[0].toInt())
}