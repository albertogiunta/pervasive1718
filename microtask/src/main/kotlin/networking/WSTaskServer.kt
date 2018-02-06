package networking

import JSONClass
import KlaxonDate
import com.beust.klaxon.Klaxon
import dateConverter
import logic.Controller
import logic.ontologies.Operation
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark.init
import spark.Spark.webSocket
import spark.kotlin.port
import utils.WSParams.TASK_ROOT_PATH
import utils.WSParams.WS_PORT
import java.io.IOException

@Suppress("unused", "UNUSED_PARAMETER")
@WebSocket
class WSTaskServer {

    private val controller = Controller.create(this)

    @OnWebSocketConnect
    fun connected(session: Session) {
    }

    @OnWebSocketClose
    fun closed(session: Session, statusCode: Int, reason: String) {
        controller.removeMember(session)
    }

    @OnWebSocketMessage
    @Throws(IOException::class)
    fun message(session: Session, message: String) {
        println(message)

        val jsonClass = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<JSONClass>(message)

        jsonClass?.let {
            with(jsonClass) {
                when (operation) {
                    Operation.ADD_MEMBER -> controller.addMember(doctor, session)
                    Operation.REMOVE_MEMBER -> controller.removeMember(doctor)
                    Operation.ADD_TASK -> controller.addTask(task, doctor)
                    Operation.REMOVE_TASK -> controller.removeTask(task)
                    Operation.CHANGE_TASK_STATUS -> controller.changeTaskStatus(task)
                }
            }
        }
        println(controller.members.keys)
    }
}

fun main(args: Array<String>) {
    port(WS_PORT)
    webSocket(TASK_ROOT_PATH, WSTaskServer::class.java)
    init()
}