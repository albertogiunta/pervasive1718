package logic

import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import model.*
import networking.WSTaskServer
import org.eclipse.jetty.websocket.api.Session
import utils.handlingGetResponse
import utils.toJson
import utils.toVisibleTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class TaskController private constructor(private val ws: WSTaskServer,
                                         val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf()) {

    lateinit var leader: Pair<Member, Session>
    val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()

    companion object {
        val dbUrl = "http://localhost:8100/api"
        val visorUrl = "http://localhost:8400/api" // TODO che porta ha il visore, che api ha il visore

        lateinit var INSTANCE: TaskController
        private val isInitialized = AtomicBoolean()
        private lateinit var activityList: MutableList<Activity>

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = TaskController(ws)
            }
            activityList = handlingGetResponse<Activity>("$dbUrl/activity/all".httpGet().responseString()).toMutableList()
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
            "$dbUrl/task/add".httpPost().body(task.toJson()).responseString()
            "$visorUrl/add".httpPost().body(task.toVisibleTask(member, activityName = activityList.first { x -> x.id == task.activityId }.name).toJson()).responseString()
        }
    }

    fun removeTask(task: Task) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                taskMemberAssociationList.remove(this)
                ws.sendMessage(members[member]!!, TaskPayload(member, TaskOperation.REMOVE_TASK, Task.emptyTask()))
                "$dbUrl/task/${it.task.id}".httpDelete().responseString()
                "$visorUrl/remove/${it.task.id}".httpDelete().responseString()
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
                "$dbUrl/task/${it.task.id}/status/${it.task.statusId}".httpPut().responseString()
            } ?: ws.sendMessage(session, TaskPayload(Member.emptyMember(), TaskOperation.ERROR_CHANGING_STATUS, task))
        }
    }
}