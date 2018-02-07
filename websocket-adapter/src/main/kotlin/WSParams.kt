import java.net.URI

object WSParams {

    const val WS_HOST = "ws://localhost:"
    const val WS_PORT = 8081
    const val WS_PATH_DEFAULT = "/"
    const val WS_PATH_TASK = "/task"
    const val WS_PATH_NOTIFIER = "/notifier"

}

object URIFactory {

    fun getDefaultURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_PORT) =
        URI("$host$port${WSParams.WS_PATH_DEFAULT}")

    fun getTaskURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_PORT) =
        URI("$host$port${WSParams.WS_PATH_TASK}")

    fun getNotifierURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_PORT) =
        URI("$host$port${WSParams.WS_PATH_NOTIFIER}")
}