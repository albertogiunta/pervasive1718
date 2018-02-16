
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import process.MicroServiceManager
import java.io.StringReader
import java.util.*


class SessionTest {

    private val startArguments = arrayOf("2")

    private var sessionDnsList: MutableList<SessionDNS> = mutableListOf()
    private var manager = MicroServiceManager()
    private lateinit var baseUrl: String
    private lateinit var dbBaseUrl: String

    @Before
    fun setUp() {
        ConfigLoader().load(startArguments)
        baseUrl = Services.Utils.defaultHostHttpPrefix(Services.SESSION)
        dbBaseUrl = Services.Utils.defaultHostHttpPrefix(Services.DATA_BASE)

        RouteController.initRoutes()
        Thread.sleep(1500)

        manager.newService(Services.DATA_BASE, "0")
        Thread.sleep(1500)
        manager.newService(Services.DATA_BASE, "1")
        Thread.sleep(1500)
        manager.newService(Services.DATA_BASE, "2")
        Thread.sleep(1500)
    }

    @After
    fun destroyAll() {
        manager.closeSession("0")
        manager.closeSession("1")
        manager.closeSession("2")
    }


    @Test
    fun createNewSessionTest() {
        sessionDnsList.clear()
        val roomId = ""
        var session = SessionDNS(-1, "", "")
        "$baseUrl/new/$roomId".httpPost().responseString().third.success { session = Klaxon().parse<SessionDNS>(it)!! }
        assertTrue(session.patId == roomId)
    }

    @Test
    fun getAllSessions() {
        handlingGetResponseWithArrayOfDnsSessions(makeGet("$baseUrl/all"))
        val previousSize = sessionDnsList.size
        println(previousSize)

        "$baseUrl/new/1".httpPost().responseString()
        Thread.sleep(500)
        "$baseUrl/new/2".httpPost().responseString()
        Thread.sleep(500)
        "$baseUrl/new/3".httpPost().responseString()

        handlingGetResponseWithArrayOfDnsSessions(makeGet("$baseUrl/all"))

        println(sessionDnsList.size)
        assertTrue(sessionDnsList.size == previousSize + 3)
    }

    @Test
    fun closeSession() {
        "$baseUrl/new/codicefiscalepaziente".httpPost().responseString()
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