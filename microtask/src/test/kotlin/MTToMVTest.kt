import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import config.ConfigLoader
import config.Services
import model.SessionDNS
import model.VisibleTask
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*

class MTtoMVTest {

    private lateinit var listResult: List<VisibleTask>

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var getAllTaskVisor: String
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
            getAllTaskVisor = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.VISORS.port}/${Connection.API}/all"
            newSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/new/gntlrt94b21g479u"
            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/close/"

            /*println()
            println("istanzio monitor")
            manager.newService(Services.MONITOR, startArguments[0]) // 8200
            Thread.sleep(3000)
            println()
            println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)*/
            println()
            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            /*println()
            println("istanzio visors")
            manager.newService(Services.VISORS, startArguments[0]) // 8400
            Thread.sleep(3000)
            println()
            println("istanzio task")
            manager.newService(Services.TASK_HANDLER, startArguments[0]) // 8200
            Thread.sleep(3000)*/
            val timeout = 5000 // 5000 milliseconds = 5 seconds.
            val readTimeout = 60000 // 60000 milliseconds = 1 minute.


            newSession.httpPost().timeout(timeout).timeoutRead(readTimeout).responseString().third.fold(success = { session = klaxon.parse<SessionDNS>(it)!!; println("ho ricevuto risposta dal db: $session") }, failure = { println("ho ricevuto un errore $it") })

            leaderWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
            memberWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }

        }

        @AfterClass
        @JvmStatic
        fun destroyAll() {
            (closeSession + session.sessionId).httpDelete().responseString().third.fold(success = { println("ho chiuso la sessione") }, failure = { println("ho ricevuto un errore in fase di chiusura della sessione: $it") })
            manager.closeSession(startArguments[0])
        }
    }



    @Test
    fun `create leader and member interaction and add task`() {
        val taskId = 32

        mockLeaderMemberInteractionAndTaskAddition(session, 4, taskId, leaderWS, memberWS)

        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString().also {
            println("La risposta è " + it.third)
        })
        println(listResult.size)
        Assert.assertTrue(listResult.firstOrNull { it.id == taskId } != null)

    }

    @Test
    fun `create leader and member interaction, add task and remove task`() {
        val taskId = 35

        mockLeaderMemberInteractionAndTaskRemoval(session, 5, 35, leaderWS, memberWS)
        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString())
        Assert.assertTrue(listResult.firstOrNull { it.id == taskId } == null)
    }
}