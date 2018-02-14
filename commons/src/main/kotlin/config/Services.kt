package config

enum class Services(val port: Int, val wsPath: String, val executableName: String, val module : String) {
    SESSION(8000, "/session", "microsession.jar", "microsession"),
    DATA_BASE(8100, "/", "microdb.jar", "microdatabase"),
    TASK_HANDLER(8200, "/task", "microtask.jar", "microtask"),
    NOTIFIER(8300, "/notifier", "microservice-notifier-0.1.jar", "micronotifier"),
    VISORS(8400, "/", "microvisors.jar", "microvisors");

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