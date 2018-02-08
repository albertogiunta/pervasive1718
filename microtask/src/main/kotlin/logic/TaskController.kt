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
            taskMemberAssociationList.add(TaskMemberAssociation.create(task, member))
            ws.sendMessage(sessionController.members[member]!!, TaskPayload(member, TaskOperation.ADD_TASK, task))
        }
    }

    override fun removeTask(task: Task) {
        with(taskMemberAssociationList.first { it.task.id == task.id }) {
            taskMemberAssociationList.remove(this)
            ws.sendMessage(sessionController.members[member]!!, TaskPayload(member, TaskOperation.REMOVE_TASK, task))
        }
    }

    override fun changeTaskStatus(newTask: Task) {
        with(taskMemberAssociationList.first { it.task.id == newTask.id }) {
            task.status = newTask.status
            ws.sendMessage(sessionController.members[member]!!, TaskPayload(member, TaskOperation.CHANGE_TASK_STATUS, task))
        }
    }
}