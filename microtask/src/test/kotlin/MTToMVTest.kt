import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import config.ConfigLoader
import config.Services
import model.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*
import java.util.concurrent.CountDownLatch

class MTtoMVTest {

    private lateinit var listResult: List<VisibleTask>

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var getAllTaskVisor: String
        private lateinit var closeSession: String
        private val manager = MicroServiceManager()
        private lateinit var leaderWS: WSClient
        private lateinit var memberWS: WSClient

        private lateinit var wsClient: WSClient
        private lateinit var session: SessionDNS
        private lateinit var latch: CountDownLatch


        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader().load(startArguments)
            getAllTaskVisor = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.VISORS.port}/${Connection.API}/all"
            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/close/"

            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)

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

            Thread.sleep(5000)

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

        val aTask = mockLeaderMemberInteractionAndTaskAddition(session, "gntlrt94b21g479u", 0, leaderWS, memberWS)

        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString().also {
            println("La risposta è " + it.third)
        })
        println(listResult.size)
        Assert.assertTrue(listResult.firstOrNull { it.name == aTask.task.name } != null)

    }

    @Test
    fun `create leader and member interaction, add task and remove task`() {

        val aTask = mockLeaderMemberInteractionAndTaskRemoval(session, "gntlrt94b21g479u", 0, leaderWS, memberWS)
        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString())
        Assert.assertTrue(listResult.firstOrNull { it.name == aTask.task.name } == null)
    }
}