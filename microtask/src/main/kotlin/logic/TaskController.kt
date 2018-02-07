package logic

import networking.WSTaskServer
import java.util.concurrent.atomic.AtomicBoolean

class TaskController private constructor(private val ws: WSTaskServer,
                                         override val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf()) : Controller {

    private val sessionController = SessionController.INSTANCE

    companion object {
        lateinit var INSTANCE: TaskController
        private val isInitialized = AtomicBoolean()

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = TaskController(ws)
            }
        }
    }

    override fun addTask(task: Task, member: Member) {
        if (sessionController.members.containsKey(member)) {
            taskMemberAssociationList += TaskMemberAssociation.create(task, member)
            ws.sendMessage(sessionController.members[member]!!, TaskPayload(member, TaskOperation.ADD_TASK, task))
        }
    }

    override fun removeTask(task: Task) {
        taskMemberAssociationList.remove(taskMemberAssociationList.first { it.task.id == task.id })
    }

    override fun changeTaskStatus(task: Task) {
        taskMemberAssociationList.first { it.task.id == task.id }.task.status = task.status
    }
}