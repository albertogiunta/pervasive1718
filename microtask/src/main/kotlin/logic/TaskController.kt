package logic

import networking.WSTaskServer
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class TaskController private constructor(private val ws: WSTaskServer,
                                         val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf()) {

    lateinit var leader: Pair<Member, Session>
    val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()

    companion object {
        lateinit var INSTANCE: TaskController
        private val isInitialized = AtomicBoolean()

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = TaskController(ws)
            }
        }
    }

    fun addLeader(member: Member, session: Session) {
        leader = Pair(member, session)
    }

    fun addMember(member: Member, session: Session) {
        members[member] = session
        ws.sendMessage(leader.second, TaskPayload(member, TaskOperation.ADD_MEMBER, Task.emptyTask()))
    }

    fun addTask(task: Task, member: Member) {
        if (members.containsKey(member)) {
            taskMemberAssociationList.add(TaskMemberAssociation.create(task, member))
            ws.sendMessage(members[member]!!, TaskPayload(member, TaskOperation.ADD_TASK, task))
        }
    }

    fun removeTask(task: Task) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                taskMemberAssociationList.remove(this)
                ws.sendMessage(members[member]!!, TaskPayload(member, TaskOperation.REMOVE_TASK, Task.emptyTask()))
            }
                    ?: ws.sendMessage(leader.second, TaskPayload(Member.emptyMember(), TaskOperation.ERROR_REMOVING_TASK, task))
        }
    }

    fun changeTaskStatus(task: Task, session: Session) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                this.task.status = task.status
                ws.sendMessage(members[member]!!, TaskPayload(member, TaskOperation.CHANGE_TASK_STATUS, this.task))
                ws.sendMessage(leader.second, TaskPayload(member, TaskOperation.CHANGE_TASK_STATUS, this.task))
            } ?: ws.sendMessage(session, TaskPayload(Member.emptyMember(), TaskOperation.ERROR_CHANGING_STATUS, task))
        }
    }
}