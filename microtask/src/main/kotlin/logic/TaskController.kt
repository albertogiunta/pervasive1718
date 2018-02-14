package logic

import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import networking.WSTaskServer
import org.eclipse.jetty.websocket.api.Session
import toJson
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class TaskController private constructor(private val ws: WSTaskServer,
                                         val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf()) {

    lateinit var leader: Pair<Member, Session>
    val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()

    val dbUrl = "http://localhost:8100/api/task"
    val visorUrl = "http://localhost:8400/api/task" // TODO che porta ha il visore, che api ha il visore

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
            "$dbUrl/add".httpPost().body(task.toJson()).responseString()
            "$visorUrl/add".httpPost().body(task.toVisibleTask(member).toJson()).responseString()
        }
    }

    fun removeTask(task: Task) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                taskMemberAssociationList.remove(this)
                ws.sendMessage(members[member]!!, TaskPayload(member, TaskOperation.REMOVE_TASK, Task.emptyTask()))
                "$dbUrl/${it.task.id}".httpDelete().responseString()
                "$visorUrl/${it.task.id}".httpDelete().responseString()
            }
                    ?: ws.sendMessage(leader.second, TaskPayload(Member.emptyMember(), TaskOperation.ERROR_REMOVING_TASK, task))
        }
    }

    fun changeTaskStatus(task: Task, session: Session) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                it.task.statusId = task.statusId
                ws.sendMessage(members[member]!!, TaskPayload(member, TaskOperation.CHANGE_TASK_STATUS, this.task))
                ws.sendMessage(leader.second, TaskPayload(member, TaskOperation.CHANGE_TASK_STATUS, this.task))
                "$dbUrl/${it.task.id}/status/${it.task.statusId}".httpPut().responseString()
            } ?: ws.sendMessage(session, TaskPayload(Member.emptyMember(), TaskOperation.ERROR_CHANGING_STATUS, task))
        }
    }
}