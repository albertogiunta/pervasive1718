import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    MicroTaskBootstrap.init()
    waitInitAndNotifyToMicroSession(args[0].toInt())
}
