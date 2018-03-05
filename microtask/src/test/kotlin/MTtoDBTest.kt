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
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*
import java.util.concurrent.CountDownLatch

class MTtoDBTest {

    private lateinit var listResult: List<model.Task>

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var readTask: String
        private lateinit var closeSession: String
        private val manager = MicroServiceManager()
        private lateinit var leaderWS: WSClient
        private lateinit var memberWS: WSClient

        private lateinit var wsClient: WSClient
        private lateinit var session: SessionDNS
        private lateinit var latch: CountDownLatch

        private lateinit var taskHistory: String

        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader().load(startArguments)
            readTask = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/task/all"
            taskHistory = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/task/history"
            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/close/"
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

        @AfterClass
        @JvmStatic
        fun destroyAll() {
            (closeSession + session.sessionId).httpDelete().responseString().third.fold(success = {
                println("ho chiuso la sessione")
            }, failure = {
                println("ho ricevuto un errore in fase di chiusura della sessione: $it")
            })
            manager.closeSession(startArguments[0])
        }
    }

    @Test
    fun `create leader and member and add task`() {
        val fetchedMaxId : Int = handlingGetResponse<Task>(taskHistory.httpGet().responseString()).map {it.id}.max()?: 0
        println(fetchedMaxId)
        mockLeaderMemberInteractionAndTaskAddition(session, "gntlrt94b21g479u", fetchedMaxId + 1, leaderWS, memberWS)
        Thread.sleep(5000L)
        listResult = handlingGetResponse(taskHistory.httpGet().responseString())
        println(listResult)
        assertTrue(listResult.firstOrNull { it.id >=  fetchedMaxId + 1 && it.id <= fetchedMaxId + 2} != null)
    }

    @Test
    fun `create leader and member, add task and remove task`() {
        val fetchedMaxId : Int = handlingGetResponse<Task>(taskHistory.httpGet().responseString()).map {it.id}.max()?: 0
        mockLeaderMemberInteractionAndTaskRemoval(session, "gntlrt94b21g479u", fetchedMaxId + 1, leaderWS, memberWS)
        Thread.sleep(5000L)
        listResult = handlingGetResponse(taskHistory.httpGet().responseString())
        assertTrue(listResult.none { it.id >=  fetchedMaxId + 1 && it.id <= fetchedMaxId + 2})
    }

    @Test
    fun `create leader and member, add task and change task status`() {
        val fetchedMaxId : Int = handlingGetResponse<Task>(taskHistory.httpGet().responseString()).map {it.id}.max()?: 0
        mockLeaderMemberInteractionAndTaskChange(session, "gntlrt94b21g479u", fetchedMaxId + 1, leaderWS, memberWS)
        Thread.sleep(5000L)
        listResult = handlingGetResponse(taskHistory.httpGet().responseString())
        println(listResult.firstOrNull { it.id >=  fetchedMaxId + 1 && it.id <= fetchedMaxId + 2 })
        assertTrue(listResult.firstOrNull { it.id >=  fetchedMaxId + 1 && it.id <= fetchedMaxId + 2 }!!.statusId == Status.FINISHED.id)
    }
}