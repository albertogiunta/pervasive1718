
import logic.*
import networking.WSTaskServer
import org.junit.Assert.*
import org.junit.Test
import spark.Spark
import spark.kotlin.port
import java.sql.Timestamp
import java.util.*

class TaskMessagingTest {

    companion object {
        private var controller: ServerController

        init {
            initServer()
            Thread.sleep(3000)
            controller = ServerControllerImpl.INSTANCE
        }

        private fun initServer() {
            port(WSParams.WS_PORT)
            Spark.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            Spark.init()
        }

        private fun initClient(): WSClient = WSClientInitializer.init(WSClient(URIFactory.getTaskURI()))
    }

    @Test
    fun memberAddedTest() {
        val initialSize = controller.members.size

        // member
        val member = addMemberThread()

        member.start()
        Thread.sleep(7000)
        assertEquals(controller.members.size, initialSize + 1)
    }

    @Test
    fun taskAssignmentTest() {
        // member
        val member = addMemberThread()

        member.start()
        Thread.sleep(1000)

        // leader
        val leader = addTaskThread(
            Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time)),
            Member(1, "Member")
        )
        leader.start()
        Thread.sleep(5000)
    }

    @Test
    fun removeMemberTest() {
        val addMember = addMemberThread()
        val removeMember = removeMemberThread()

        addMember.start()
        Thread.sleep(3000)
        removeMember.start()
        Thread.sleep(3000)
        assertFalse(controller.members.containsKey(defaultMember()))
        assertTrue(controller.members.isEmpty())
    }

    @Test
    fun removeTaskTest() {
        val addMember = addMemberThread()
        val addTask = addTaskThread(
            defaultTask(),
            defaultMember()
        )
        val removeTask = removeTaskThread(defaultTask())
        addMember.start()
        Thread.sleep(3000)
        addTask.start()
        Thread.sleep(3000)
        removeTask.start()
        Thread.sleep(3000)
        assertFalse(controller.taskMemberAssociationList.contains(TaskMemberAssociation.create(
            defaultTask(),
            defaultMember()))
        )
        assertTrue(controller.taskMemberAssociationList.isEmpty())
    }

    @Test
    fun changeTaskStatusTest() {
        val addMember = addMemberThread()
        val addTask = addTaskThread(
            defaultTask(),
            defaultMember()
        )
        val taskChanged = defaultTask().also { it.status = Status.FINISHED }
        val changeTaskStatus = changeTaskStatus(taskChanged)
        addMember.start()
        Thread.sleep(3000)
        addTask.start()
        Thread.sleep(3000)
        changeTaskStatus.start()
        Thread.sleep(3000)
        assertTrue(controller.taskMemberAssociationList.first { it.task.id == taskChanged.id }.task.status == Status.FINISHED)
    }

    private fun addMemberThread(id: Int = 1, member: String = "Member"): Thread {
        return Thread({
            initializeConnection().send(TaskPayload(Member(id, member), Operation.ADD_MEMBER, emptyTask()).toJson()
            )
        })
    }

    private fun removeMemberThread(id: Int = 1, member: String = "Member"): Thread {
        return Thread({
            initializeConnection().send(TaskPayload(Member(id, member), Operation.REMOVE_MEMBER, emptyTask()).toJson()
            )
        })
    }

    private fun addTaskThread(task: Task, member: Member): Thread {
        return Thread({
            initializeConnection().send(TaskPayload(member, Operation.ADD_TASK, task).toJson())
        })
    }

    private fun removeTaskThread(task: Task): Thread {
        return Thread({
            initializeConnection().send(TaskPayload(emptyMember(), Operation.REMOVE_TASK, task).toJson())
        })
    }

    private fun changeTaskStatus(task: Task): Thread {
        return Thread({
            initializeConnection().send(TaskPayload(emptyMember(), Operation.CHANGE_TASK_STATUS, task).toJson())
        })
    }

    private fun initializeConnection(): WSClient {
        return WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
    }
}