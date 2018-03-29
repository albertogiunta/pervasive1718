package logic

import Params
import ResponseHandlers
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
    val members = ConcurrentHashMap<Member, Session>()

    companion object {
        private var configNotCompleted = true
        val dbUrl = Services.Utils.defaultHostUrlApi(Services.DATA_BASE)
        val visorUrl = Services.Utils.defaultHostUrlApi(Services.VISORS)

        lateinit var INSTANCE: TaskController
        private val isInitialized = AtomicBoolean()
        private lateinit var activityList: MutableList<Activity>
        private lateinit var operatorList: MutableList<Operator>

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = TaskController(ws)

            }
        }

        fun fetchActivitiesAndOperatorsFromDB() {
            while (configNotCompleted) {
                var responseString = "$dbUrl/${Params.Activity.API_NAME}".httpGet().responseString()
                responseString.third.fold(
                    success = { configNotCompleted = false },
                    failure = {
                        println("MicroTask: the MicroDatabase doesn't respond, reissuing the request in "
                                + WAIT_TIME_BEFORE_THE_NEXT_REQUEST + "microseconds")
                        Thread.sleep(WAIT_TIME_BEFORE_THE_NEXT_REQUEST)
                    }
                )
                activityList = handlingGetResponse<Activity>(responseString).toMutableList()

                responseString = "$dbUrl/${Params.Operator.API_NAME}".httpGet().responseString()
                responseString.third.fold(
                    success = { configNotCompleted = false },
                    failure = {}
                )
                operatorList = handlingGetResponse<Operator>(responseString).toMutableList()
            }
        }
    }

    private fun getMemberWithNameSurnameFromOperatorsByCF(cf: String): MemberWithNameSurname {
        return try {
            operatorList.filter { it.operatorCF == cf }.map { MemberWithNameSurname(it.operatorCF, it.name, it.surname) }.first()
        } catch (e: NoSuchElementException) {
            MemberWithNameSurname(cf, "", "")
        }
    }

    fun addLeader(member: Member, session: Session) {
        leader = Pair(member, session)
        ws.sendMessage(leader.second, PayloadWrapper(Services.instanceId(), WSOperations.LEADER_RESPONSE, "ok"))
    }

    fun addMember(member: Member, session: Session) {
        if (members.containsKey(member)) {
            members[member] = session
            val augmentedMember = AugmentedMemberFromServer(
                getMemberWithNameSurnameFromOperatorsByCF(member.userCF),
                taskMemberAssociationList.filter { it.member.userCF == member.userCF && it.task.statusId != Status.FINISHED.id }.map { it.task.toAugmentedTask(activityList) }.toMutableList())
            val message = PayloadWrapper(Services.instanceId(), WSOperations.MEMBER_COMEBACK_RESPONSE, augmentedMember.toJson())
            ws.sendMessage(members[member]!!, message)
        } else {
            members[member] = session
            if (leader.second.isOpen) {
                val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.ADD_MEMBER_NOTIFICATION, (getMemberWithNameSurnameFromOperatorsByCF(member.userCF)).toJson())
                ws.sendMessage(leader.second, message)
            }
        }
    }

    fun getAllMembers() {
        val list = mutableListOf<AugmentedMemberFromServer>()
        if (members.isNotEmpty()) {
            members.keys.toList().forEach { member ->
                list.add(AugmentedMemberFromServer(getMemberWithNameSurnameFromOperatorsByCF(member.userCF), taskMemberAssociationList.filter { it.member.userCF == member.userCF && it.task.statusId != Status.FINISHED.id }.map { it.task.toAugmentedTask(activityList) }.toMutableList()))
            }
        }
        val message = PayloadWrapper(Services.instanceId(), WSOperations.LIST_MEMBERS_RESPONSE, AugmentedMembersAdditionNotification(list).toJson())
        ws.sendMessage(leader.second, message)
    }

    fun addTask(augmentedTask: AugmentedTask, member: Member) {
        if (members.containsKey(member)) {
            taskMemberAssociationList.add(TaskMemberAssociation.create(augmentedTask.task, member))
            val message = PayloadWrapper(Services.instanceId(),
                WSOperations.ADD_TASK, TaskAssignment(member, augmentedTask).toJson())
            ws.sendMessage(members[member]!!, message)
            ws.sendMessage(leader.second, message)

            "$dbUrl/${Params.Task.API_NAME}".httpPost().body(augmentedTask.task.toJson()).responseString {
                request, response, result ->  ResponseHandlers.emptyHandler(request, response, result)
            }
            "$visorUrl/${Params.Task.API_NAME}".httpPost()
                    .body(augmentedTask.task.toVisibleTask(getMemberWithNameSurnameFromOperatorsByCF(member.userCF), activityName = activityList.first { x -> x.id == augmentedTask.task.activityId }.name).toJson())
                    .responseString {
                        request, response, result ->  ResponseHandlers.emptyHandler(request, response, result)
                    }
        }
    }

    fun removeTask(augmentedTask: AugmentedTask) {
        with(taskMemberAssociationList.firstOrNull { it.task.name == augmentedTask.task.name }) {
            this?.let {
                augmentedTask.task.statusId = Status.ELIMINATED.id
                taskMemberAssociationList.remove(this)
                val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.REMOVE_TASK, TaskAssignment(member, augmentedTask).toJson())
                ws.sendMessage(members[member]!!, message)
                ws.sendMessage(leader.second, message)

                "$dbUrl/${Params.Task.API_NAME}/${Params.Task.TASK_NAME}/${it.task.name}".httpDelete().responseString {
                    request, response, result ->  ResponseHandlers.emptyHandler(request, response, result)
                }
            }
                    ?: ws.sendMessage(leader.second, PayloadWrapper(Services.instanceId(),
                        WSOperations.ERROR_REMOVING_TASK, TaskError(augmentedTask.task, "").toJson()))
        }
    }

    fun changeTaskStatus(augmentedTask: AugmentedTask, session: Session) {
        with(taskMemberAssociationList.firstOrNull { it.task.name == augmentedTask.task.name }) {
            this?.let {
                it.task.statusId = augmentedTask.task.statusId
                val message = PayloadWrapper(Services.instanceId(),
                    WSOperations.CHANGE_TASK_STATUS, TaskAssignment(member, this.task.toAugmentedTask(activityList)).toJson())
                ws.sendMessage(members[member]!!, message)
                ws.sendMessage(leader.second, message)
                "$dbUrl/${Params.Task.API_NAME}/${Params.Task.TASK_NAME}/${it.task.name}".httpPut().body(it.task.toJson()).responseString()
                if (augmentedTask.task.statusId == Status.FINISHED.id) {
                    "$dbUrl/${Params.Task.API_NAME}/${Params.Task.STOP}/${it.task.name}".httpPut().body(augmentedTask.task.toJson()).responseString {
                        request, response, result ->  ResponseHandlers.emptyHandler(request, response, result)
                    }
                    "$visorUrl/${Params.Task.API_NAME}/${it.task.name}".httpDelete().responseString {
                        request, response, result ->  ResponseHandlers.emptyHandler(request, response, result)
                    }
                }
            } ?: ws.sendMessage(session, PayloadWrapper(Services.instanceId(),
                WSOperations.ERROR_CHANGING_STATUS, StatusError(augmentedTask.task.statusId, augmentedTask.task, "").toJson()))
        }
    }

    fun getAllActivities() {
        if (activityList.isNotEmpty()) {
            val message = PayloadWrapper(Services.instanceId(),
                WSOperations.SET_ALL_ACTIVITIES, ActivityAdditionNotification(activityList.sortedBy { it.name }).toJson())
            ws.sendMessage(leader.second, message)
        }
    }
}