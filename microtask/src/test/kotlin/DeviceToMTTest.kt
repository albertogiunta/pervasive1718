
import logic.*
import logic.Member.Companion.emptyMember
import networking.WSTaskServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import spark.kotlin.ignite
import java.sql.Timestamp
import java.util.*

class DeviceToMTTest {

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
    fun addLeaderTest() {
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)
        assertEquals(taskController.leader.first.id, -1)
        assertEquals(taskController.leader.first.name, "Leader")
    }

    @Test
    fun memberJoinsSessionTest() {

        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)
        val initialSize = taskController.members.size

        addMemberThread(memberId = 1).start()
        Thread.sleep(3000)
        addMemberThread(memberId = 2).start()
        Thread.sleep(3000)

        assertEquals(taskController.members.size, initialSize + 2)
    }

    @Test
    fun taskAssignmentTest() {
        //leader
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member(3,"Member")
        // member
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = Task(1,"Task assegnato",Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time+1))

        addTaskThread(task, member).start()
        Thread.sleep(4000)

        println(taskController.taskMemberAssociationList)
        assertTrue(taskController.taskMemberAssociationList.firstOrNull{it.task.id == task.id && it.member.id == member.id}!=null)
    }

    @Test
    fun removeTaskTest() {
        Thread.sleep(2000)
        //leader
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member.defaultMember()

        // member
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        addTaskThread(Task.defaultTask(), Member.defaultMember()).start()
        Thread.sleep(3000)

        assertTrue(taskController.taskMemberAssociationList.size == 1)

        removeTaskThread(Task.defaultTask()).start()
        Thread.sleep(3000)
        assertTrue(taskController.taskMemberAssociationList.isEmpty())
    }

    @Test
    fun changeTaskStatusTest() {
        //leader
        addLeaderThread(memberId = -1).start()
        Thread.sleep(1000)

        val member = Member.defaultMember()

        // member
        addMemberThread(memberId = member.id).start()
        Thread.sleep(1000)

        addTaskThread(Task.defaultTask(), Member.defaultMember()).start()
        Thread.sleep(1000)

        val taskChanged = Task.defaultTask().apply { this.status = Status.FINISHED }

        changeTaskStatus(taskChanged).start()
        Thread.sleep(3000)
        println(taskController.members)

        assertTrue(taskController.taskMemberAssociationList.first { it.task.id == taskChanged.id }.task.status == Status.FINISHED)
    }

    private fun addLeaderThread(memberId: Int): Thread {
        return Thread({
            initializeConnectionWithTaskWSClient()
                    .send(TaskPayload(Member(memberId, "Leader"), TaskOperation.ADD_LEADER, Task.emptyTask()).toJson())
        })
    }
    private fun addMemberThread(memberId: Int): Thread {
        return Thread({
            initializeConnectionWithTaskWSClient()
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


    private fun initializeConnectionWithTaskWSClient(): WSClient {
        return WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
    }
}