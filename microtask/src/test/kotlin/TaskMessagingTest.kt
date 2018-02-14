
import logic.*
import logic.Member.Companion.emptyMember
import networking.WSTaskServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import spark.kotlin.ignite
import java.sql.Timestamp
import java.util.*

class TaskMessagingTest {

    companion object {
        private var taskController: TaskController

        init {
            val taskService = ignite()
            taskService.port(WSParams.WS_TASK_PORT)
            taskService.service.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            taskService.service.init()

            Thread.sleep(1000)

            taskController = TaskController.INSTANCE

        }
    }

    @Test
    fun createSessionAndAddLeaderTest() {
        // member
        Thread.sleep(3000)

        assertEquals(taskController.leader.first.id, 0)
        assertEquals(taskController.leader.first.name, "Leader")
    }

    @Test
    fun memberJoinsSessionTest() {
        val initialSize = taskController.members.size

        // member

        // member
        addMemberThread(memberId = 0).start()
        Thread.sleep(3000)
        // member
        addMemberThread(memberId = 1).start()
        Thread.sleep(3000)

        assertEquals(taskController.members.size, initialSize + 2)
    }

    @Test
    fun taskAssignmentTest() {
        // leader

        val member = Member(1, "Member")

        // member
        addMemberThread(memberId = member.id).start()
        Thread.sleep(1000)

        // member
        addTaskThread(Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time)), member).start()
        Thread.sleep(3000)
    }

    @Test
    fun removeTaskTest() {

        // leader

        val member = Member(1, "Member")

        // member
        addMemberThread(memberId = member.id).start()
        Thread.sleep(1000)

        addTaskThread(Task.defaultTask(), Member.defaultMember()).start()
        Thread.sleep(1000)

        removeTaskThread(Task.defaultTask()).start()
        Thread.sleep(3000)

        assertTrue(taskController.taskMemberAssociationList.isEmpty())
    }

    @Test
    fun changeTaskStatusTest() {
        // leader
        Thread.sleep(3000)

        val member = Member(1, "Member")

        // member
        addMemberThread(memberId = member.id).start()
        Thread.sleep(1000)

        addTaskThread(Task.defaultTask(), Member.defaultMember()).start()
        Thread.sleep(1000)

        val taskChanged = Task.defaultTask().apply { this.status = Status.FINISHED }

        changeTaskStatus(taskChanged).start()
        Thread.sleep(3000)

        assertTrue(taskController.taskMemberAssociationList.first { it.task.id == taskChanged.id }.task.status == Status.FINISHED)
    }

//    @Test
//    fun removeMemberTest() {
//        val addMember = addMemberThread()
//        val removeMember = removeMemberThread()
//
//        addMember.start()
//        Thread.sleep(3000)
//        removeMember.start()
//        Thread.sleep(3000)
//        assertFalse(taskController.members.containsKey(Member.defaultMember()))
//        assertTrue(taskController.members.isEmpty())
//    }

    private fun addMemberThread(sessionId: Int = 0, memberId: Int): Thread {
        return Thread({
            initializeConnectionWithSessionWSClient()
                .send(TaskPayload(Member(memberId, "Member"), TaskOperation.ADD_MEMBER, Task.emptyTask()).toJson())
        })
    }

    private fun addTaskThread(task: Task, member: Member): Thread {
        return Thread({
            initializeConnectionWithTaskWSClient().send(TaskPayload(member, TaskOperation.ADD_TASK, task).toJson())
        })
    }

    private fun removeTaskThread(task: Task): Thread {
        return Thread({
            initializeConnectionWithTaskWSClient().send(TaskPayload(emptyMember(), TaskOperation.REMOVE_TASK, task).toJson())
        })
    }

    private fun changeTaskStatus(task: Task): Thread {
        return Thread({
            initializeConnectionWithTaskWSClient().send(TaskPayload(emptyMember(), TaskOperation.CHANGE_TASK_STATUS, task).toJson())
        })
    }

    private fun initializeConnectionWithSessionWSClient(): WSClient {
        return WSClientInitializer.init(WSClient(URIFactory.getSessionURI())).also { Thread.sleep(2000) }
    }

    private fun initializeConnectionWithTaskWSClient(): WSClient {
        return WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
    }
}