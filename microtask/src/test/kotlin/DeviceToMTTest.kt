import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import config.ConfigLoader
import config.Services
import logic.TaskController
import model.SessionDNS
import model.Status
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*

class DeviceToMTTest {

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var taskController: TaskController

        private lateinit var newSession: String
        private lateinit var closeSession: String
        private val manager = MicroServiceManager()
        private val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        private lateinit var session: SessionDNS

        private lateinit var leaderWS: WSClient
        private lateinit var memberWS: WSClient


        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader().load(startArguments)
            newSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/new/gntlrt94b21g479u"
            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/close/"

            /*println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)
            println()*/
            println("istanzio task")
            MicroTaskBootstrap.init().also { Thread.sleep(4000) } //need to be started in this way to access the INSTANCE
            taskController = TaskController.INSTANCE
            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            println()


            newSession.httpPost().responseString().third.fold(success = { session = klaxon.parse<SessionDNS>(it)!!; println("ho ricevuto risposta dal db: $session") }, failure = { println("ho ricevuto un errore $it") })
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
        mockLeader(-1, leaderWS)
        assertEquals(taskController.leader.first.id, -1)
        assertEquals(taskController.leader.first.name, "Leader")
    }

    @Test
    fun `create leader WS and member WS and test handshake from member to leader`() {

        val initialSize = taskController.members.size
        mockLeaderAndMembers(4, leaderWS, memberWS)
        assertEquals(taskController.members.size, initialSize + 2)
    }

    @Test
    fun `create leader, create member, and test if leader assign test to member`() {
        val taskId = 32
        val memberId = 64
        mockLeaderMemberInteractionAndTaskAddition(session, 64, taskId, leaderWS, memberWS)

        println(taskController.taskMemberAssociationList)
        assertTrue(taskController.taskMemberAssociationList.firstOrNull{it.task.id == taskId && it.member.id == memberId}!=null)
    }

    @Test
    fun `create leader, create member, assign and remove task`() {
        val taskId = 41
        mockLeaderMemberInteractionAndTaskRemoval(session, 4, taskId, leaderWS, memberWS)
        assertTrue(taskController.taskMemberAssociationList.firstOrNull{ it.task.id == taskId} == null)
    }

    @Test
    fun `create leader, create member, assign task and change task's status`() {
        val taskId = 50
        mockLeaderMemberInteractionAndTaskChange(session, 4, taskId, leaderWS, memberWS)
        assertTrue(taskController.taskMemberAssociationList.first { it.task.id == taskId }.task.statusId == Status.FINISHED.id)
    }


}