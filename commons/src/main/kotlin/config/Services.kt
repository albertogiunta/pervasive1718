package config


/*Remeber to load the configuration with ConfigLoader*/
class Services(var port: Int, val wsPath: String, val executableName: String, val module: String) {

    companion object {
        lateinit var SESSION: Services
        lateinit var DATA_BASE: Services
        lateinit var TASK_HANDLER: Services
        lateinit var NOTIFIER: Services
        lateinit var VISORS: Services
        lateinit var MONITOR: Services

        fun loadServicesConfig(loadedConfig: Config) {
            Services.SESSION = Services(loadedConfig.microSession.port,
                    "/session",
                    loadedConfig.microSession.jarName,
                    "microsession")
            Services.DATA_BASE = Services(loadedConfig.microDatabase.port,
                    "/",
                    loadedConfig.microDatabase.jarName,
                    "microdatabase")
            Services.TASK_HANDLER = Services(loadedConfig.microTask.port,
                    "/task",
                    loadedConfig.microTask.jarName,
                    "microtask")
            Services.NOTIFIER = Services(loadedConfig.microNotifier.port,
                    "/notifier",
                    loadedConfig.microNotifier.jarName,
                    "micronotifier")
            Services.VISORS = Services(loadedConfig.microVisors.port,
                    "/",
                    loadedConfig.microVisors.jarName,
                    "microvisors")
            Services.MONITOR = Services(loadedConfig.microMonitor.port,
                    "/",
                    loadedConfig.microMonitor.jarName,
                    "micromonitor")
        }

        fun values(): Array<Services> = arrayOf(SESSION, DATA_BASE, TASK_HANDLER, NOTIFIER, VISORS, MONITOR)
    }

    object Utils {
        const val maxSimultaneousSessions = 10
        const val defaultHost = "localhost"
        const val defaultWSPath = "/"
        
        object Protocols {
            const val websocket = "ws"
            const val http = "http"
            const val https = "https"
        }

        object WSParams {
            const val defaultWSRoot = "${Protocols.websocket}://$defaultHost:"
        }

        object RESTParams {
            const val applicationJson = "application/json"
        }
    }
}