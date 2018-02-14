package networking

import WSServer
import logic.*
import logic.Serializer.klaxon
import  KlaxonDate
import dateConverter
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
class WSTaskServer : WSServer<TaskPayload>() {

    init {
        TaskController.init(this)
    }

    private val controller = TaskController.INSTANCE

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)

        val taskPayload = klaxon.fieldConverter(KlaxonDate::class,dateConverter).parse<TaskPayload>(message)

        taskPayload?.let {
            with(taskPayload) {
                when (taskOperation) {
                    TaskOperation.ADD_LEADER -> controller.addLeader(member, session) // done by leader
                    TaskOperation.ADD_MEMBER -> controller.addMember(member, session) // done by member
                    TaskOperation.ADD_TASK -> controller.addTask(task, member) // done by leader
                    TaskOperation.REMOVE_TASK -> controller.removeTask(task) // done by leader
                    TaskOperation.CHANGE_TASK_STATUS -> controller.changeTaskStatus(task, session) // done by both
                }
            }
        }
    }

    fun sendMessage(session: Session, member: Member, operation: TaskOperation, task: Task) {
        sendMessage(session, TaskPayload(member, operation, task))
    }
}