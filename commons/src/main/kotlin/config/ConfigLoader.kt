package config

import GsonInitializer
import java.io.File
import java.io.InputStream

/**
 * Read the properties from the config file
 */
class ConfigLoader(val configPath: String = "config.json") {
    private val inputStream: InputStream = File(configPath).inputStream()
    private val inputString = inputStream.bufferedReader().use { it.readText() }
    val loadedConfig = GsonInitializer.gson.fromJson(inputString, Config::class.java)

    fun load() {
        Services.loadServicesConfig(loadedConfig)
    }
}

data class Config(val microSession: BasicServiceConfig,
                  val microDatabase: BasicServiceConfig,
                  val microTask: BasicServiceConfig,
                  val microNotifier: BasicServiceConfig,
                  val microVisors: BasicServiceConfig,
                  val microMonitor: BasicServiceConfig)

data class BasicServiceConfig(val port: Int,
                              val jarName: String)

