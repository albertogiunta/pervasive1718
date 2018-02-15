
import logic.*
import model.Member
import model.Status
import model.Task
import networking.WSTaskServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import process.MicroServiceManager
import spark.kotlin.ignite
import utils.*
import java.sql.Timestamp
import java.util.*

class DeviceToMTTest {

    companion object {
        private var taskController: TaskController
        private val manager = MicroServiceManager(System.getProperty("user.dir"))

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

        val task = Task(6, -1, member.id, Timestamp(Date().time), Timestamp(Date().time+1),1, Status.RUNNING.id)

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

        val task = Task(3, -1, member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)
        addTaskThread(task, Member.defaultMember()).start()
        Thread.sleep(3000)

        assertTrue(taskController.taskMemberAssociationList.size == 1)

        removeTaskThread(task).start()
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

        val task = Task(4, -1, member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)
        addTaskThread(task, Member.defaultMember()).start()
        Thread.sleep(1000)

        val taskChanged = task.apply { this.statusId = Status.FINISHED.id }

        changeTaskStatus(taskChanged).start()
        Thread.sleep(3000)
        println(taskController.members)

        assertTrue(taskController.taskMemberAssociationList.first { it.task.id == taskChanged.id }.task.statusId == Status.FINISHED.id)
    }


}