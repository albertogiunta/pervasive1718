package networking

import logic.*
import logic.Serializer.klaxon
import logic.ServerControllerImpl.Companion.TASK_ROOT_PATH
import logic.ServerControllerImpl.Companion.WS_PORT
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark.init
import spark.Spark.webSocket
import spark.kotlin.port
import java.io.IOException

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
class WSTaskServer {

    init {
        ServerControllerImpl.init(this)
    }

    private val controller: Controller = ServerControllerImpl.INSTANCE
    private val log = WSLogger(WSLogger.WSUser.SERVER)

    @OnWebSocketConnect
    fun connected(session: Session) {
        log.printStatusMessage("session opened")
    }

    @OnWebSocketClose
    fun closed(session: Session, statusCode: Int, reason: String) {
        log.printStatusMessage("session closed | exit code $statusCode | info: $reason")
        controller.removeMember(session)
    }

    @OnWebSocketMessage
    @Throws(IOException::class)
    fun message(session: Session, message: String) {
        log.printIncomingMessage(message)

        val jsonClass = klaxon.parse<TaskPayload>(message)

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
    }

    fun sendMessage(session: Session, member: Member, operation: Operation, task: Task) {
        sendMessage(session, TaskPayload(member, operation, task))
    }

    fun sendMessage(session: Session, taskPayload: TaskPayload) {
        sendMessage(session, taskPayload.toJson())
    }

    private fun sendMessage(session: Session, message: String) {
        log.printOutgoingMessage(message)
        session.remote.sendString(message)
    }
}

fun main(args: Array<String>) {
    port(WS_PORT)
    webSocket(TASK_ROOT_PATH, WSTaskServer::class.java)
    init()
}