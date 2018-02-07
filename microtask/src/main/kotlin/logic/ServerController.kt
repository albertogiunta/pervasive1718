package logic

import networking.WSTaskServer
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The interface representing the contract of the microservice
 */
interface Controller {

    val taskMemberAssociationList: MutableList<TaskMemberAssociation>
    val members: Map<Member, Session>

    fun addMember(member: Member, session: Session)

    fun removeMember(member: Member)

    fun removeMember(session: Session)

    fun addTask(task: Task, member: Member)

    fun removeTask(task: Task)

    fun changeTaskStatus(task: Task)

}

class ServerControllerImpl private constructor(private val ws: WSTaskServer,
                                               override val taskMemberAssociationList: MutableList<TaskMemberAssociation> = mutableListOf(),
                                               override val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()) : Controller {

    companion object {
        const val HOST = "ws://localhost:"
        const val WS_PORT = 8081
        const val TASK_ROOT_PATH = "/task"

        lateinit var INSTANCE: ServerControllerImpl
        val isInitialized = AtomicBoolean()

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = ServerControllerImpl(ws)
            }
        }
    }

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
            ws.sendMessage(members[member]!!, member, Operation.ADD_TASK, task)
        }
    }

    override fun removeTask(task: Task) {
        taskMemberAssociationList.remove(taskMemberAssociationList.first { it.task == task })
    }

    override fun changeTaskStatus(task: Task) {
        taskMemberAssociationList.first { it.task.id == task.id }.task.status = task.status
    }
}