import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import Params.Log.TABLE_NAME
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import config.ConfigLoader
import config.Services
import model.*
import model.Serializer.klaxon
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.KlaxonDate
import utils.dateConverter
import utils.handlingGetResponse
import utils.toJson
import java.util.concurrent.CountDownLatch

class MMToMDTest {

    companion object {
        private val startArguments = arrayOf("0")
        private val manager = MicroServiceManager()
        private lateinit var wsClient: WSClient
        private lateinit var closeSession: String
        private lateinit var getAllLogs: String
        private lateinit var session: SessionDNS
        private lateinit var latch: CountDownLatch

        private var logList = listOf<Log>()

        @BeforeClass
        @JvmStatic
        fun setup() {
            ConfigLoader("../config.json").load(startArguments)

            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/"
            getAllLogs = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/$TABLE_NAME"

            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)

            wsClient = object : WSClient(URIFactory.getSessionURI(port = 8501)) {
                override fun onMessage(message: String) {
                    super.onMessage(message)
                    val sessionWrapper = Serializer.klaxon.fieldConverter(KlaxonDate::class, dateConverter).parse<PayloadWrapper>(message)
                    session = klaxon.parse<SessionDNS>(sessionWrapper!!.body) ?: SessionDNS(-1, "no", -1,"EmptyLeaderCF").also { println("NON HO INIZIALIZZATO LA SESSION PERCHè NON HO CAPITO IL MESSAGGIO DELLA WS: $message") }
                    latch.countDown()
                }
            }.also { WSClientInitializer.init(it) }

            Thread.sleep(5000)
        }

        @AfterClass
        @JvmStatic
        fun closeConnection() {
            manager.closeSession(startArguments[0])
        }
    }

    @Test
    fun `add new session and start listening to monitor and write data`() {
        latch = CountDownLatch(1)
        wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment("gntlrt94b21g479u", "asdflkjasdflkj").toJson()).toJson())
        latch.await()
        logList = handlingGetResponse(getAllLogs.httpGet().responseString())
        val startingSize = logList.size

        Thread.sleep(10000)

        Thread.sleep(5000)
        logList = handlingGetResponse(getAllLogs.httpGet().responseString())
        val newSize = logList.size
        assertTrue(newSize > startingSize)
        (closeSession + session.sessionId).httpDelete().responseString()

        Thread.sleep(5000)
    }
}