import java.net.URI

object WSParams {

    const val WS_HOST = "ws://localhost:"
    const val WS_SESSION_PORT = 8000
    const val WS_TASK_PORT = 8081
    const val WS_NOTIFIER_PORT = 8082
    const val WS_PATH_DEFAULT = "/"
    const val WS_PATH_TASK = "/task"
    const val WS_PATH_SESSION = "/session"
    const val WS_PATH_NOTIFIER = "/notifier"

}

object URIFactory {

    fun getDefaultURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_TASK_PORT) =
        URI("$host$port${WSParams.WS_PATH_DEFAULT}")

    fun getSessionURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_SESSION_PORT) =
        URI("$host$port${WSParams.WS_PATH_SESSION}")

    fun getTaskURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_TASK_PORT) =
        URI("$host$port${WSParams.WS_PATH_TASK}")

    fun getNotifierURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_TASK_PORT) =
        URI("$host$port${WSParams.WS_PATH_NOTIFIER}")
}