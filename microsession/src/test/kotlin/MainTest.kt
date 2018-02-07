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
import org.junit.Assert.assertTrue
import org.junit.Test
import spark.kotlin.port
import java.io.StringReader
import java.util.*

class SessionTest {

    private var listResult: MutableList<Session> = mutableListOf()
    private val baseUrl = "http://localhost:8000/session"

    companion object {
        init {
            port(8000)
            RouteController.initRoutes()
            Thread.sleep(4000)
        }
    }

    @Test
    fun createNewSessionTest() {
        listResult.clear()
        val roomId = 0
        var session = Session(-1, -1, "")
        "$baseUrl/new/$roomId".httpPost().responseString().third.success { session = Klaxon().parse<Session>(it)!! }
        assertTrue(session.roomId == roomId)
    }

    @Test
    fun getAllSessions() {
        handlingGetResponseWithArray(makeGet("$baseUrl/all"))
        val previousSize = listResult.size

        "$baseUrl/new/1".httpPost().responseString()
        "$baseUrl/new/2".httpPost().responseString()
        "$baseUrl/new/3".httpPost().responseString()

        handlingGetResponseWithArray(makeGet("$baseUrl/all"))

        assertTrue(listResult.size == previousSize + 3)
    }

    @Test
    fun closeSession() {
        "$baseUrl/new/1".httpPost().responseString()
        handlingGetResponseWithArray(makeGet("$baseUrl/all"))
        val previousSize = listResult.size

        "$baseUrl/close/1".httpDelete().responseString()
        handlingGetResponseWithArray(makeGet("$baseUrl/all"))

        assertTrue(listResult.size == previousSize - 1)
    }

    private fun makeGet(string: String): Triple<Request, Response, Result<String, FuelError>> {
        return string.httpGet().responseString()
    }

    private fun handlingGetResponseWithArray(triplet: Triple<Request, Response, Result<String, FuelError>>) {
        triplet.third.fold(success = {
            val klaxon = Klaxon()
            JsonReader(StringReader(it)).use { reader ->
                reader.beginArray {
                    listResult.clear()
                    while (reader.hasNext()) {
                        val session = klaxon.parse<Session>(reader)!!
                        (listResult as ArrayList<Session>).add(session)
                    }
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }

}