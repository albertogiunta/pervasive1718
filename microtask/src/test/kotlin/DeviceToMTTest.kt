import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.github.kittinunf.fuel.httpDelete
import config.ConfigLoader
import config.Services
import logic.TaskController
import model.*
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*
import java.util.concurrent.CountDownLatch

class DeviceToMTTest {

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var taskController: TaskController

        private lateinit var closeSession: String
        private val manager = MicroServiceManager()

        private lateinit var wsClient: WSClient
        private lateinit var session: SessionDNS
        private lateinit var latch: CountDownLatch

        private lateinit var leaderWS: WSClient
        private lateinit var memberWS: WSClient


        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader().load(startArguments)
            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/close/"

//            println(" In this test is normal if the system throw 2 bind Exceptions,\n" +
//                    " because internally MicroSession will try to start 2 another\n" +
//                    " Microdatabase and Microtask process already started indipendently \n" +
//                    " by the test\n")

            Thread({
                println("STARTED TASK")
                MicroTaskBootstrap.init(startArguments)
                Thread.sleep(3000) //need to be started in this way to access the INSTANCE
                taskController = TaskController.INSTANCE
                println("FINISHED STARTING TASK")
            }).start()

            Thread.sleep(5000)
            println("STARTED SESSION")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(5000)

            latch = CountDownLatch(1)
            wsClient = object : WSClient(URIFactory.getSessionURI(port = 8501)) {
                override fun onMessage(message: String) {
                    super.onMessage(message)
                    println("RECEIVED MESSAGE ON CLIENT WS $message")
                    val sessionWrapper = Serializer.klaxon.fieldConverter(KlaxonDate::class, dateConverter).parse<PayloadWrapper>(message)
                    session = Serializer.klaxon.parse<SessionDNS>(sessionWrapper!!.body)!!
                    latch.countDown()
                }
            }.also { WSClientInitializer.init(it) }
            wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment("1", "-1").toJson()).toJson())
            latch.await()

            Thread.sleep(3000)
            leaderWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
            memberWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
            Thread.sleep(3000)
        }

        @AfterClass
        @JvmStatic
        fun destroyAll() {
            (closeSession + session.sessionId).httpDelete().responseString()
            Thread.sleep(5000)
            manager.closeSession(startArguments[0])
        }

    }

    @Test
    fun `create leader WS and test connection`(){
        mockLeader("Leader", leaderWS)
        assertEquals("Leader", taskController.leader.first.userCF)
    }

    @Test
    fun `create leader WS and member WS and test handshake from member to leader`() {
        taskController.members.clear()
        val initialSize = taskController.members.size
        mockLeaderAndMembers("Member", leaderWS, memberWS)
        assertEquals(initialSize + 2, taskController.members.size)
    }

    @Test
    fun `create leader, create member, and test if leader assign task to member`() {
        val taskId = 32
        val userCF = "Member"
        mockLeaderMemberInteractionAndTaskAddition(session, userCF, taskId, leaderWS, memberWS)

        println(taskController.taskMemberAssociationList)
        assertTrue(taskController.taskMemberAssociationList.firstOrNull { it.task.id == taskId && it.member.userCF == userCF } != null)
    }

    @Test
    fun `create leader, create member, assign and remove task`() {
        val taskId = 41
        mockLeaderMemberInteractionAndTaskRemoval(session, "Member", taskId, leaderWS, memberWS)
        assertTrue(taskController.taskMemberAssociationList.firstOrNull{ it.task.id == taskId} == null)
    }

    @Test
    fun `create leader, create member, assign task and change task's status`() {
        val taskId = 50
        mockLeaderMemberInteractionAndTaskChange(session, "Member", taskId, leaderWS, memberWS)
        assertTrue(taskController.taskMemberAssociationList.first { it.task.id == taskId }.task.statusId == Status.FINISHED.id)
    }


}