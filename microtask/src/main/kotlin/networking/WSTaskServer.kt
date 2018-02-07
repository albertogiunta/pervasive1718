package networking

import WSParams
import WSServer
import WSServerInitializer
import logic.*
import logic.Serializer.klaxon
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.io.IOException

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
class WSTaskServer : WSServer<TaskPayload>() {

    init {
        ServerControllerImpl.init(this)
    }

    private val serverController: ServerController = ServerControllerImpl.INSTANCE

    @OnWebSocketClose
    override fun closed(session: Session, statusCode: Int, reason: String) {
        super.closed(session, statusCode, reason)
        serverController.removeMember(session)
    }

    @OnWebSocketMessage
    @Throws(IOException::class)
    override fun message(session: Session, message: String) {
        super.message(session, message)

        val jsonClass = klaxon.parse<TaskPayload>(message)

        jsonClass?.let {
            with(jsonClass) {
                when (operation) {
                    Operation.ADD_MEMBER -> serverController.addMember(doctor, session)
                    Operation.REMOVE_MEMBER -> serverController.removeMember(doctor)
                    Operation.ADD_TASK -> serverController.addTask(task, doctor)
                    Operation.REMOVE_TASK -> serverController.removeTask(task)
                    Operation.CHANGE_TASK_STATUS -> serverController.changeTaskStatus(task)
                }
            }
        }
    }

    fun sendMessage(session: Session, member: Member, operation: Operation, task: Task) {
        sendMessage(session, TaskPayload(member, operation, task))
    }

}

fun main(args: Array<String>) {
    WSServerInitializer.init(serverClazz = WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK)
}