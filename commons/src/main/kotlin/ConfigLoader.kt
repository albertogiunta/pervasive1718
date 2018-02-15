import utils.GsonInitializer
import java.io.File
import java.io.InputStream

/**
 * Read the properties from the config file
 */
object ConfigLoader {
    private val inputStream: InputStream = File("config.json").inputStream()
    private val inputString = inputStream.bufferedReader().use { it.readText() }

    val loadedConfig = GsonInitializer.gson.fromJson(inputString, Config::class.java)
}

data class Config(val microSession: MicroServiceConfig,
                  val microDatabase: MicroServiceConfig,
                  val microTask: MicroServiceConfig,
                  val microNotifier: MicroServiceConfig,
                  val microVisors: MicroServiceConfig)

data class MicroServiceConfig(val port: Int,
                              val jarName: String)

fun main(argv: Array<String>) {
    println(ConfigLoader.loadedConfig)
}