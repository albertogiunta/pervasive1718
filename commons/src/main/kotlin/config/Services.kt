package config

import utils.calculatePort
import java.net.URI


/*Remember to load the configuration with ConfigLoader*/
class Services private constructor(var port: Int, val wsPath: String, val executableName: String, val module: String) {

    fun root() : String = "${Services.SESSION.wsPath}/${Services.instanceId()}${this.wsPath}"

    fun wsURI(host : String = Utils.defaultHost) : URI =
            URI("${Utils.Protocols.websocket}://$host:${this.port}${root()}")

    fun httpURI(host : String = Utils.defaultHost) : URI =
            URI("${Utils.Protocols.http}://$host:${this.port}${root()}")

    companion object {

        private var instanceId : Int = 0
        private var startedIndependently: Boolean = false

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

        fun valuesWithoutSession(): Array<Services> = arrayOf(DATA_BASE, TASK_HANDLER, NOTIFIER, VISORS, MONITOR)


        fun instanceId(): Int = instanceId

        fun isNotStartedIndependently() = !startedIndependently

        fun finalizeConfiguration(args: Array<String>) {
            if (args.isNotEmpty()) {
                if (args[0] != "-si") {
                    instanceId = args[0].toInt()

                    Services.SESSION.port = Services.SESSION.port
                    Services.DATA_BASE.port = Services.DATA_BASE.calculatePort(args)
                    Services.TASK_HANDLER.port = Services.TASK_HANDLER.calculatePort(args)
                    Services.NOTIFIER.port = Services.NOTIFIER.calculatePort(args)
                    Services.VISORS.port = Services.VISORS.calculatePort(args)
                    Services.MONITOR.port = Services.MONITOR.calculatePort(args)
                }
                if ((args.size == 2 && args[0] == "-si" && args[1] == "true") ||
                        ((args.size == 3 && args[1] == "-si" && args[2] == "true"))) {
                    startedIndependently = true
                }
            }
        }
    }

    object Utils {
        var WAIT_TIME_BEFORE_THE_NEXT_REQUEST = 2000L
        const val maxSimultaneousSessions = 5
        const val defaultHost = "localhost"
        const val defaultWSPath = "/"
        
        object Protocols {
            const val websocket = "ws"
            const val http = "http"
            const val https = "https"
        }

        fun defaultHostHttpPrefix(serv: Services) = "${Protocols.http}://$defaultHost:${serv.port}${Services.SESSION.wsPath}"

        fun defaultHostUrlApi(serv: Services) = "${Protocols.http}://$defaultHost:${serv.port}/api"

        object WSParams {
            const val defaultWSRoot = "${Protocols.websocket}://$defaultHost:"
        }

        object RESTParams {
            const val applicationJson = "application/json"
        }
    }
}