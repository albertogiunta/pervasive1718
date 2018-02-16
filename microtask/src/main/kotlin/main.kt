import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load(args)
    MicroTaskBootstrap.init()
}
