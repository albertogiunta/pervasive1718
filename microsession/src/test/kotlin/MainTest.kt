import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.success
import config.ConfigLoader
import config.Services
import model.SessionDNS
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import java.io.StringReader
import java.util.*


class SessionTest {

    private var sessionDnsList: MutableList<SessionDNS> = mutableListOf()
    private var killFunction: () -> Any = {}

    companion object {

        private val startArguments = arrayOf("0")
        private val manager = MicroServiceManager()
        private lateinit var baseUrl: String

        @BeforeClass
        @JvmStatic
        fun setUp() {
            ConfigLoader().load(startArguments)

            baseUrl = Services.Utils.defaultHostHttpPrefix(Services.SESSION)

            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            println()
//            RouteController.initRoutes()
//            Thread.sleep(1500)
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
        sessionDnsList.clear()
        val patientId = "frodo"
        val leaderId = -1
        var session = SessionDNS(-1, "", "")
        "$baseUrl/new/$patientId/leaderid/$leaderId".httpPost().responseString().third.success { session = Klaxon().parse<SessionDNS>(it)!! }
        println("$baseUrl/new/$patientId/leaderid/$leaderId")
        Thread.sleep(3000)
        killFunction = {
            "$baseUrl/close/${session.sessionId}".httpDelete().responseString()
            println("$baseUrl/close/${session.sessionId}")
            Thread.sleep(4000)
        }
        assertTrue(session.patId == patientId)
    }

    @Test
    fun getAllSessions() {
        handlingGetResponseWithArrayOfDnsSessions(makeGet("$baseUrl/all"))
        val previousSize = sessionDnsList.size
        println(previousSize)
        var session1 = SessionDNS(-1, "", "")
        var session2 = SessionDNS(-1, "", "")
        var session3 = SessionDNS(-1, "", "")

        "$baseUrl/new/1/leaderid/-1".httpPost().responseString().third.success { session1 = Klaxon().parse<SessionDNS>(it)!! }
        Thread.sleep(500)
        "$baseUrl/new/2/leaderid/-1".httpPost().responseString().third.success { session2 = Klaxon().parse<SessionDNS>(it)!! }
        Thread.sleep(500)
        "$baseUrl/new/3/leaderid/-1".httpPost().responseString().third.success { session3 = Klaxon().parse<SessionDNS>(it)!! }

        Thread.sleep(3000)
        handlingGetResponseWithArrayOfDnsSessions(makeGet("$baseUrl/all"))

        killFunction = {
            "$baseUrl/close/${session1.sessionId}".httpDelete().responseString()
            Thread.sleep(500)
            "$baseUrl/close/${session2.sessionId}".httpDelete().responseString()
            Thread.sleep(500)
            "$baseUrl/close/${session3.sessionId}".httpDelete().responseString()
            Thread.sleep(4000)
        }

        println(sessionDnsList.size)
        assertTrue(sessionDnsList.size == previousSize + 3)
    }

    @Test
    fun closeSession() {
        "$baseUrl/new/codicefiscalepaziente/leaderid/-1".httpPost().responseString()
        handlingGetResponseWithArrayOfDnsSessions(makeGet("$baseUrl/all"))
        val sessionId = sessionDnsList.first().sessionId
        val previousSize = sessionDnsList.size

        "$baseUrl/close/$sessionId".httpDelete().responseString()
        handlingGetResponseWithArrayOfDnsSessions(makeGet("$baseUrl/all"))

        assertTrue(sessionDnsList.size == previousSize - 1)
    }

    private fun makeGet(string: String): Triple<Request, Response, Result<String, FuelError>> {
        return string.httpGet().responseString()
    }

    private fun handlingGetResponseWithArrayOfDnsSessions(triplet: Triple<Request, Response, Result<String, FuelError>>) {
        triplet.third.fold(success = {
            val klaxon = Klaxon()
            JsonReader(StringReader(it)).use { reader ->
                reader.beginArray {
                    sessionDnsList.clear()
                    while (reader.hasNext()) {
                        val session = klaxon.parse<SessionDNS>(reader)!!
                        (sessionDnsList as ArrayList<SessionDNS>).add(session)
                    }
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }
}