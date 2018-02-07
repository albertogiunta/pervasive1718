package networking

import WSParams
import WSServer
import WSServerInitializer
import logic.*
import logic.Serializer.klaxon
import org.eclipse.jetty.websocket.api.Session

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
class WSTaskServer : WSServer<TaskPayload>() {

    init {
        TaskController.init(this)
    }

    private val controller: Controller = TaskController.INSTANCE

    override fun message(session: Session, message: String) {
        super.message(session, message)

        val taskPayload = klaxon.parse<TaskPayload>(message)

        taskPayload?.let {
            with(taskPayload) {
                when (taskOperation) {
                    TaskOperation.ADD_TASK -> controller.addTask(task, member)
                    TaskOperation.REMOVE_TASK -> controller.removeTask(task)
                    TaskOperation.CHANGE_TASK_STATUS -> controller.changeTaskStatus(task)
                }
            }
        }
    }

    fun sendMessage(session: Session, member: Member, operation: TaskOperation, task: Task) {
        sendMessage(session, TaskPayload(member, operation, task))
    }
}

fun main(args: Array<String>) {
    WSServerInitializer.init(serverClazz = WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK)
    WSServerInitializer.init(serverClazz = WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK)
}