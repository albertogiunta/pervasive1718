import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroTaskBootstrap.init(WSParams.WS_SESSION_PORT)
}
