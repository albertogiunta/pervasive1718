import config.ConfigLoader
import config.Services
import logic.TaskController
import model.Member
import model.Status
import model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*
import java.sql.Timestamp
import java.util.*

class DeviceToMTTest {

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var taskController: TaskController
        private val manager = MicroServiceManager()


        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader().load(startArguments)
            println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)

            println()
            println("istanzio microtask")
            MicroTaskBootstrap.init().also { Thread.sleep(2000) }
            taskController = TaskController.INSTANCE
        }

    }

    @Test
    fun `create leader WS and test connection`() {
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)
        assertEquals(taskController.leader.first.id, -1)
        assertEquals(taskController.leader.first.name, "Leader")
    }

    @Test
    fun `create leader WS and member WS and test handshake from member to leader`() {

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
    fun `create leader, create member, and test if leader assign test to member`() {
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
    fun `create leader, create member, assign and remove task`() {
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
    fun `create leader, create member, assign task and change task's status`() {
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