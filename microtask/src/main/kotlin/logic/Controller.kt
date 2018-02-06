package logic

import networking.WSTaskServer
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap

/**
 * The interface representing the contract of the microservice
 */
interface Controller {

    fun addMember(member: Member, session: Session)

    fun removeMember(member: Member)

    fun removeMember(session: Session)

    fun addTask(task: Task, member: Member)

    fun removeTask(task: Task)

    fun changeTaskStatus(task: Task)

    companion object {
        fun create(ws: WSTaskServer) = ControllerImpl(ws)
    }

}

class ControllerImpl(private val ws: WSTaskServer) : Controller {
    private val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf()
    val members = ConcurrentHashMap<Member, Session>()


    override fun addMember(member: Member, session: Session) {
        members[member] = session
    }

    override fun removeMember(member: Member) {
        members.remove(member)
    }

    override fun removeMember(session: Session) {
        members.keySet(session).forEach { members.remove(it) }
    }

    override fun addTask(task: Task, member: Member) {
        if (members.containsKey(member)) {
            taskMemberAssociationList + TaskMemberAssociation.create(task, member)
        }
    }

    override fun removeTask(task: Task) {
        taskMemberAssociationList.remove(taskMemberAssociationList.first { it.task == task })
    }

    override fun changeTaskStatus(task: Task): Unit {
        taskMemberAssociationList.first { it.task.id == task.id }.task.status = task.status
    }
}