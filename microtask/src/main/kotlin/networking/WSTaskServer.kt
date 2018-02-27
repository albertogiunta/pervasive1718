package networking

import WSServer
import logic.TaskController
import model.*
import model.Serializer.klaxon
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.KlaxonDate
import utils.dateConverter

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
class WSTaskServer : WSServer<PayloadWrapper>() {

    init {
        TaskController.init(this)
    }

    private val controller = TaskController.INSTANCE

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)
        val taskWrapper = klaxon.fieldConverter(KlaxonDate::class, dateConverter).parse<PayloadWrapper>(message)

        taskWrapper?.let {
            with(taskWrapper) {
                when (subject) {
                    WSOperations.ADD_LEADER -> {
                        val notification: MembersAdditionNotification = taskWrapper.objectify(body)
                        //TODO GESTIRE CADUTA LEADER
                        if (notification.members.isNotEmpty()) {
                            controller.addLeader(notification.members.first(), session)
                        }
                    } // done by leader
                    WSOperations.ADD_MEMBER -> {
                        val notification: MembersAdditionNotification = taskWrapper.objectify(body)
                        if (notification.members.isNotEmpty()) {
                            controller.addMember(notification.members.first(), session)
                        }
                    } // done by member
                    WSOperations.ADD_TASK -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.addTask(assignment.task, assignment.member)
                    } // done by leader
                    WSOperations.REMOVE_TASK -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.removeTask(assignment.task)
                    } // done by leader
                    WSOperations.CHANGE_TASK_STATUS -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.changeTaskStatus(assignment.task, session)
                    } // done by both
                    WSOperations.GET_ALL_ACTIVITIES -> {
                        val notification: MembersAdditionNotification = taskWrapper.objectify(body)
                        controller.getAllActivities(notification.members.first(), session)
                    } // done by leader
                    else -> println("Message was not handled " + message)
                }
            }
        }
    }
}