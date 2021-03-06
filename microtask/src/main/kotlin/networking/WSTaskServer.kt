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
class WSTaskServer : WSServer<PayloadWrapper>("Task") {

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
                        val leader: Member = taskWrapper.objectify(body)
                        controller.addLeader(leader, session)
                    } // done by leader
                    WSOperations.ADD_MEMBER -> {
                        val newMember: Member = taskWrapper.objectify(body)
                        controller.addMember(newMember, session)
                    } // done by member
                    WSOperations.LIST_MEMBERS_REQUEST -> {
                        controller.getAllMembers()
                    } // done by leader
                    WSOperations.ADD_TASK -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        println("Assigning Task ${assignment.augmentedTask.activityName} to ${assignment.member.userCF}")
                        controller.addTask(assignment.augmentedTask, assignment.member)
                    } // done by leader
                    WSOperations.REMOVE_TASK -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.removeTask(assignment.augmentedTask)
                    } // done by leader
                    WSOperations.CHANGE_TASK_STATUS -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.changeTaskStatus(assignment.augmentedTask, session)
                    } // done by both
                    WSOperations.GET_ALL_ACTIVITIES -> {
                        controller.getAllActivities()
                    } // done by leader
                    else -> println("Message was not handled " + message)
                }
            }
        }
    }
}