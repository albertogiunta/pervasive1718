import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import config.ConfigLoader
import config.Services
import model.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import process.MicroServiceManager
import utils.*
import java.util.concurrent.CountDownLatch

class MTtoDBTest {

    private lateinit var listResult: List<model.Task>
    private val startArguments = arrayOf("0")
    private lateinit var closeSession: String
    private val manager = MicroServiceManager()
    private lateinit var leaderWS: WSClient
    private lateinit var memberWS: WSClient

    private lateinit var wsClient: WSClient
    private lateinit var session: SessionDNS
    private lateinit var latch: CountDownLatch

    private lateinit var taskHistory: String

    @Before
    fun setup() {
        ConfigLoader().load(startArguments)
        taskHistory = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/${Params.Task.API_NAME}"
        closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/${Params.Session.API_NAME}/"
        println("istanzio session")
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

        Thread.sleep(5000)

        leaderWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
        memberWS = WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }

        Thread.sleep(3000)
    }

    @After
    fun destroyAll() {
        (closeSession + session.sessionId).httpDelete().responseString().third.fold(success = {
            println("ho chiuso la sessione")
        }, failure = {
            println("ho ricevuto un errore in fase di chiusura della sessione: $it")
        })
        manager.closeSession(startArguments[0])
    }


    @Test
    fun `create leader and member and add task`() {
        val aTask = mockLeaderMemberInteractionAndTaskAddition(session, "gntlrt94b21g479u", 0, leaderWS, memberWS)
        Thread.sleep(5000L)
        listResult = handlingGetResponse(taskHistory.httpGet().responseString())
        println(listResult)
        assertTrue(listResult.firstOrNull { it.name == aTask.task.name } != null)
    }

    @Test
    fun `create leader and member, add task and remove task`() {
        val aTask = mockLeaderMemberInteractionAndTaskRemoval(session, "gntlrt94b21g479u", 0, leaderWS, memberWS)
        Thread.sleep(5000L)
        listResult = handlingGetResponse(taskHistory.httpGet().responseString())
        assertTrue(listResult.none { it.name == aTask.task.name })
    }

    @Test
    fun `create leader and member, add task and change task status`() {

        val aTask = mockLeaderMemberInteractionAndTaskChange(session, "gntlrt94b21g479u", 0, leaderWS, memberWS)
        Thread.sleep(5000L)
        listResult = handlingGetResponse(taskHistory.httpGet().responseString())
        assertTrue(listResult.firstOrNull { it.name == aTask.task.name }!!.statusId == Status.FINISHED.id)
    }
}