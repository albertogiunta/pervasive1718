import config.ConfigLoader

fun main(args: Array<String>) {
    ConfigLoader().load()
    MicroSessionBootstrap.init(config.Services.SESSION.port)
}