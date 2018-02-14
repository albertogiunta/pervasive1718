import config.Services
import config.Services.Utils
import java.net.URI

object WSParams {

    val WS_HOST = Utils.WSParams.defaultWSRoot
    val WS_SESSION_PORT = Services.SESSION.port
    val WS_TASK_PORT = Services.TASK_HANDLER.port
    val WS_NOTIFIER_PORT = Services.NOTIFIER.port
    val WS_PATH_DEFAULT = Utils.defaultWSPath
    val WS_PATH_TASK = Services.TASK_HANDLER.wsPath
    val WS_PATH_SESSION = Services.SESSION.wsPath
    val WS_PATH_NOTIFIER = Services.NOTIFIER.wsPath

}

object URIFactory {

    fun getDefaultURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_TASK_PORT) =
        URI("$host$port${WSParams.WS_PATH_DEFAULT}")

    fun getSessionURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_SESSION_PORT) =
        URI("$host$port${WSParams.WS_PATH_SESSION}")

    fun getTaskURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_TASK_PORT) =
        URI("$host$port${WSParams.WS_PATH_TASK}")

    fun getNotifierURI(host: String = WSParams.WS_HOST, port: Int = WSParams.WS_NOTIFIER_PORT) =
        URI("$host$port${WSParams.WS_PATH_NOTIFIER}")
}