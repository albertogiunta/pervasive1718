package logic

import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import config.Services
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
        var WAIT_TIME_BEFORE_THE_NEXT_REQUEST = 2000L
        var configNotCompleted = true
        val dbUrl = Services.Utils.defaultHostUrlApi(Services.DATA_BASE)
        val visorUrl = Services.Utils.defaultHostUrlApi(Services.VISORS)

        lateinit var INSTANCE: TaskController
        private val isInitialized = AtomicBoolean()
        private lateinit var activityList: MutableList<Activity>

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = TaskController(ws)
            }

            /**
             * Continue to repeat the request until the Microdatabase service responds correctly.
             * This protect the MicroTask service configuration procedure when it starts before the MicroDatabase service
             * */
            while (configNotCompleted) {
                var responseString = "$dbUrl/activity/all".httpGet().responseString()
                responseString.third.fold(
                        success = { configNotCompleted = false },
                        failure = {
                            println("MicroTask: the MicroDatabase doesn't respond, reissuing the request in "
                                    + WAIT_TIME_BEFORE_THE_NEXT_REQUEST + "microseconds")
                            Thread.sleep(WAIT_TIME_BEFORE_THE_NEXT_REQUEST)
                        }
                )
                activityList = handlingGetResponse<Activity>(responseString).toMutableList()
            }
        }
    }

    fun addLeader(member: Member, session: Session) {
        leader = Pair(member, session)
        // How to send something that is not a Task?

        //the following is for the websocket android test
        //val message = PayloadWrapper(Services.instanceId()
        //,
        //        WSOperations.ADD_LEADER, "ok")
        if (members.isNotEmpty()) {
            val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.LIST_MEMBERS, MembersAdditionNotification(members.keys().toList()).toJson())
            ws.sendMessage(leader.second, message)
            //leader.second.remote.sendString(members.keys().toList().toJson())
        }
        //ws.sendMessage(leader.second, message)
        ws.sendMessage(leader.second, PayloadWrapper(Services.instanceId(),WSOperations.LEADER_RESPONSE,"ok"))
    }

    fun addMember(member: Member, session: Session) {
        members[member] = session
        if (leader.second.isOpen) {
            val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.ADD_MEMBER, MembersAdditionNotification(members.keys().toList()).toJson())
            ws.sendMessage(leader.second, message)
        }
    }

    fun addTask(task: Task, member: Member) {
        if (members.containsKey(member)) {
            taskMemberAssociationList.add(TaskMemberAssociation.create(task, member))
            val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.ADD_TASK, TaskAssignment(member, task).toJson())
            ws.sendMessage(members[member]!!, message)
            "$dbUrl/task/add".httpPost().body(task.toJson()).responseString()
            "$visorUrl/add".httpPost().body(task.toVisibleTask(member, activityName = activityList.first { x -> x.id == task.activityId }.name).toJson()).responseString()
        }
    }

    fun removeTask(task: Task) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                taskMemberAssociationList.remove(this)
                val message = PayloadWrapper(Services.instanceId(),
                        WSOperations.REMOVE_TASK, TaskAssignment(member, task).toJson())
                ws.sendMessage(members[member]!!, message)
                "$dbUrl/task/${it.task.id}".httpDelete().responseString()
                "$visorUrl/remove/${it.task.id}".httpDelete().responseString()
            }
                    ?: ws.sendMessage(leader.second, PayloadWrapper(Services.instanceId(),
                            WSOperations.ERROR_REMOVING_TASK, TaskError(task, "").toJson()))
        }
    }

    fun changeTaskStatus(task: Task, session: Session) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == task.id }) {
            this?.let {
                it.task.statusId = task.statusId
                val message = PayloadWrapper(Services.instanceId(),
                        WSOperations.CHANGE_TASK_STATUS, TaskAssignment(member, this.task).toJson())
                ws.sendMessage(members[member]!!, message)
                ws.sendMessage(leader.second, message)
                "$dbUrl/task/${it.task.id}/status/${it.task.statusId}".httpPut().responseString()
            } ?: ws.sendMessage(session, PayloadWrapper(Services.instanceId(),
                    WSOperations.ERROR_CHANGING_STATUS, StatusError(task.statusId, task, "").toJson()))
        }
    }

    fun getAllActivities(member: Member, session: Session) {
        leader = Pair(member, session)
        if (activityList.isNotEmpty()) {
            val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.SET_ALL_ACTIVITIES, ActivityAdditionNotification(activityList).toJson())
            ws.sendMessage(leader.second, message)
        }
    }
}