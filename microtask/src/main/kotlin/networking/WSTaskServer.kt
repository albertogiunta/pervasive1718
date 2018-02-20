package networking

import PayloadWrapper
import WSServer
import logic.TaskController
import model.Member
import model.MembersAdditionNotification
import model.Serializer.klaxon
import model.TaskAssignment
import model.TaskOperations
import objectify
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
                    TaskOperations.ADD_LEADER -> {
                        val member: Member = taskWrapper.objectify(body)
                        controller.addLeader(member, session)
                    } // done by leader
                    TaskOperations.ADD_MEMBER -> {
                        val member: MembersAdditionNotification = taskWrapper.objectify(body)
                        controller.addMember(member, session)
                    } // done by member
                    TaskOperations.ADD_TASK -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.addTask(assignment.body, assignment.subject)
                    } // done by leader
                    TaskOperations.REMOVE_TASK -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.removeTask(assignment.body)
                    } // done by leader
                    TaskOperations.CHANGE_TASK_STATUS -> {
                        val assignment: TaskAssignment = taskWrapper.objectify(body)
                        controller.changeTaskStatus(assignment.body, session)
                    } // done by both
                    else -> println("Message was not handled " + message)
                }
            }
        }
    }
}