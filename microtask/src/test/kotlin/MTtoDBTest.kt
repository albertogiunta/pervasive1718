import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import config.ConfigLoader
import config.Services
import model.SessionDNS
import model.Status
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*

class MTtoDBTest {

    private lateinit var listResult: List<model.Task>

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var readTask: String
        private lateinit var newSession: String
        private val manager = MicroServiceManager()
        private val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        private lateinit var session: SessionDNS
        private lateinit var leaderWS: WSClient
        private lateinit var memberWS: WSClient


        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader().load(startArguments)
            readTask = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/task/all"
            newSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/new/gntlrt94b21g479u"

            println()
            println("istanzio monitor") // needed cause otherwise MD doesn't work
            manager.newService(Services.MONITOR, startArguments[0]) // 8600
            Thread.sleep(3000)
            println()
            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            println()
            println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)
            println()
            println("istanzio task")
            manager.newService(Services.TASK_HANDLER, startArguments[0]) // 8200
            Thread.sleep(3000)

            newSession.httpPost().responseString().third.fold(success = { session = klaxon.parse<SessionDNS>(it)!!; println("ho ricevuto risposta dal db: $session") }, failure = { println("ho ricevuto un errore $it") })

            leaderWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
            memberWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }

        }

        @AfterClass
        @JvmStatic
        fun destroyAll() {
            manager.closeSession(startArguments[0])
        }
    }

    @Test
    fun `create leader and member and add task`() {
        val taskId = 32
        mockLeaderMemberInteractionAndTaskAddition(session, 4, taskId, leaderWS, memberWS)
        listResult = handlingGetResponse(readTask.httpGet().responseString())
        assertTrue(listResult.firstOrNull { it.id == taskId } != null)
    }

    @Test
    fun `create leader and member, add task and remove task`() {
        val taskId = 41
        mockLeaderMemberInteractionAndTaskRemoval(session, 4, taskId, leaderWS, memberWS)
        listResult = handlingGetResponse(readTask.httpGet().responseString())
        assertTrue(listResult.firstOrNull { it.id == taskId } == null)
    }

    @Test
    fun `create leader and member, add task and change task status`() {
        val taskId = 50
        mockLeaderMemberInteractionAndTaskChange(session, 4, taskId, leaderWS, memberWS)
        listResult = handlingGetResponse(readTask.httpGet().responseString())
        assertTrue(listResult.firstOrNull { it.id == taskId }!!.statusId == Status.FINISHED.id)
    }
}