package logic

import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import config.Services
import config.Services.Utils.WAIT_TIME_BEFORE_THE_NEXT_REQUEST
import model.*
import networking.WSTaskServer
import org.eclipse.jetty.websocket.api.Session
import utils.handlingGetResponse
import utils.toAugmentedTask
import utils.toJson
import utils.toVisibleTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class TaskController private constructor(private val ws: WSTaskServer,
                                         val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf()) {

    lateinit var leader: Pair<Member, Session>
    val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()

    companion object {
        private var configNotCompleted = true
        val dbUrl = Services.Utils.defaultHostUrlApi(Services.DATA_BASE)
        val visorUrl = Services.Utils.defaultHostUrlApi(Services.VISORS)

        lateinit var INSTANCE: TaskController
        private val isInitialized = AtomicBoolean()
        private lateinit var activityList: MutableList<Activity>

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = TaskController(ws)
            }
        }

        fun fetchActivitiesFromDB() {
            while (configNotCompleted) {
                val responseString = "$dbUrl/activity/all".httpGet().responseString()
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

    fun getAllMembers() {
        if (members.isNotEmpty()) {
            val list = mutableListOf<AugmentedMemberFromServer>()
            members.keys.toList().forEach { member ->
                list.add(AugmentedMemberFromServer(member.userCF, taskMemberAssociationList.filter { it.member.userCF == member.userCF }.map { it.task.toAugmentedTask(activityList) }.toMutableList()))
            }

            val message = PayloadWrapper(Services.instanceId(), WSOperations.LIST_MEMBERS_RESPONSE, AugmentedMembersAdditionNotification(list).toJson())
            ws.sendMessage(leader.second, message)
        }
    }

    fun addTask(augmentedTask: AugmentedTask, member: Member) {
        if (members.containsKey(member)) {
            taskMemberAssociationList.add(TaskMemberAssociation.create(augmentedTask.task, member))
            val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.ADD_TASK, TaskAssignment(member, augmentedTask).toJson())
            ws.sendMessage(members[member]!!, message)
            ws.sendMessage(leader.second, message)

            "$dbUrl/task/add".httpPost().body(augmentedTask.task.toJson()).responseString()
            "$visorUrl/add".httpPost().body(augmentedTask.task.toVisibleTask(member, activityName = activityList.first { x -> x.id == augmentedTask.task.activityId }.name).toJson()).responseString()
        }
    }

    fun removeTask(augmentedTask: AugmentedTask) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == augmentedTask.task.id }) {
            this?.let {
                taskMemberAssociationList.remove(this)
                val message = PayloadWrapper(Services.instanceId(),
                        WSOperations.REMOVE_TASK, TaskAssignment(member, augmentedTask).toJson())
                ws.sendMessage(members[member]!!, message)
                ws.sendMessage(leader.second, message)

                "$dbUrl/task/${it.task.id}".httpDelete().responseString()
                "$visorUrl/remove/${it.task.id}".httpDelete().responseString()
            }
                    ?: ws.sendMessage(leader.second, PayloadWrapper(Services.instanceId(),
                            WSOperations.ERROR_REMOVING_TASK, TaskError(augmentedTask.task, "").toJson()))
        }
    }

    fun changeTaskStatus(augmentedTask: AugmentedTask, session: Session) {
        with(taskMemberAssociationList.firstOrNull { it.task.id == augmentedTask.task.id }) {
            this?.let {
                it.task.statusId = augmentedTask.task.statusId
                val message = PayloadWrapper(Services.instanceId(),
                        WSOperations.CHANGE_TASK_STATUS, TaskAssignment(member, this.task.toAugmentedTask(activityList)).toJson())
                ws.sendMessage(members[member]!!, message)
                ws.sendMessage(leader.second, message)
                "$dbUrl/task/${it.task.id}/status/${it.task.statusId}".httpPut().responseString()
            } ?: ws.sendMessage(session, PayloadWrapper(Services.instanceId(),
                    WSOperations.ERROR_CHANGING_STATUS, StatusError(augmentedTask.task.statusId, augmentedTask.task, "").toJson()))
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