import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.result.Result
import config.ConfigLoader
import config.Services
import model.*
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson
import java.io.StringReader
import java.util.*
import java.util.concurrent.CountDownLatch


class SessionTest {

    private var killFunction: () -> Any = {}

    companion object {

        private val startArguments = arrayOf("0")
        private val manager = MicroServiceManager()
        private lateinit var baseUrl: String
        private lateinit var wsClient: WSClient
        private var sessionList = mutableListOf<SessionDNS>()
        private lateinit var latch: CountDownLatch

        @BeforeClass
        @JvmStatic
        fun setUp() {
            ConfigLoader().load(startArguments)

            baseUrl = Services.Utils.defaultHostUrlSession(Services.SESSION) + "/"+ Params.Session.API_NAME

            println("istanzio sessionList")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)

            wsClient = object : WSClient(URIFactory.getSessionURI(port = 8501)) {
                override fun onMessage(message: String) {
                    super.onMessage(message)
                    val sessionWrapper = Serializer.klaxon.fieldConverter(KlaxonDate::class, dateConverter).parse<PayloadWrapper>(message)
                    sessionList.add(Serializer.klaxon.parse<SessionDNS>(sessionWrapper!!.body)
                            ?: SessionDNS(-1, "no", -1,"emptyLeaderCF").also { println("NON HO INIZIALIZZATO LA SESSION PERCHè NON HO CAPITO IL MESSAGGIO DELLA WS: $message") })
                    latch.countDown()
                }
            }.also { WSClientInitializer.init(it) }
        }

        @AfterClass
        @JvmStatic
        fun closeConnection() {
            manager.closeSession(startArguments[0])
        }
    }

    @After
    fun killThemAll() {
        killFunction()
        killFunction = {}
    }

    @Test
    fun createNewSessionTest() {
        sessionList.clear()
        latch = CountDownLatch(1)
        val patient = "gntlrt94b21g479u"
        val leader = "asdflkjasdflkj"
        wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment(patient, leader).toJson()).toJson())
        latch.await()

        Thread.sleep(3000)
        killFunction = {
            println(baseUrl)
            "$baseUrl/${sessionList.first().sessionId}".httpDelete().responseString()
            Thread.sleep(4000)
        }
        assertTrue(sessionList.first().patientCF == patient)
    }

    @Test
    fun getAllSessions() {
        handlingGetResponseWithArrayOfDnsSessions(makeGet(baseUrl))
        val previousSize = sessionList.size
        println(previousSize)

        latch = CountDownLatch(3)
        wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment("1", "-1").toJson()).toJson())
        Thread.sleep(500)
        wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment("2", "-1").toJson()).toJson())
        Thread.sleep(500)
        wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment("3", "-1").toJson()).toJson())
        latch.await()

        Thread.sleep(3000)
        handlingGetResponseWithArrayOfDnsSessions(makeGet(baseUrl))

        killFunction = {
            sessionList.takeLast(3).forEach {
                "$baseUrl/${it.sessionId}".httpDelete().responseString()
                Thread.sleep(500)
            }
            Thread.sleep(4000)
        }

        println(sessionList.size)
        assertTrue(sessionList.size == previousSize + 3)
    }

    @Test
    fun closeSession() {
        latch = CountDownLatch(1)
        wsClient.sendMessage(PayloadWrapper(-1, WSOperations.NEW_SESSION, SessionAssignment("1", "-1").toJson()).toJson())
        latch.await()

        handlingGetResponseWithArrayOfDnsSessions(makeGet(baseUrl))
        val sessionId = sessionList.first().sessionId
        val previousSize = sessionList.size

        "$baseUrl/$sessionId".httpDelete().responseString()
        handlingGetResponseWithArrayOfDnsSessions(makeGet(baseUrl))

        assertTrue(sessionList.size == previousSize - 1)
    }

    private fun makeGet(string: String): Triple<Request, Response, Result<String, FuelError>> {
        return string.httpGet().responseString()
    }

    private fun handlingGetResponseWithArrayOfDnsSessions(triplet: Triple<Request, Response, Result<String, FuelError>>) {
        triplet.third.fold(success = {
            val klaxon = Klaxon()
            JsonReader(StringReader(it)).use { reader ->
                reader.beginArray {
                    sessionList.clear()
                    while (reader.hasNext()) {
                        val session = klaxon.parse<SessionDNS>(reader)!!
                        (sessionList as ArrayList<SessionDNS>).add(session)
                    }
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }
}