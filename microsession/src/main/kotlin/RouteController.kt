@file:Suppress("UNUSED_PARAMETER")

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import config.Services
import config.Services.Utils
import model.Session
import model.SessionDNS
import process.MicroServiceManager
import spark.Request
import spark.Response
import spark.Spark.path
import spark.Spark.port
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post
import utils.GsonInitializer
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson

object RouteController {

    fun initRoutes() {

        port(Services.SESSION.port)

        path("/session") {
            // la fa il leader, ritorna il riferimento di [MT], crea una websocket per mandargli i membri mano a mano che arrivano, ti ritorna anche l'id della sessione
            post("/new/:patId", Utils.RESTParams.applicationJson) { SessionApi.createNewSession(request, response) }

            // la fa il leader, deve sapere quale chiudere (la prende dalla new)
            delete("/close/:sessionId", Utils.RESTParams.applicationJson) { SessionApi.closeSessionById(request, response) }

            // la fanno i membri, e deve restituire la lista di interventi disponibili
            get("/all", Utils.RESTParams.applicationJson) { SessionApi.listAllSessions(request, response) }
        }
    }
}


object SessionApi {

    private val BOOT_WAIT_TIME = 10000L
    private val MAX_CONCURRENT_SESSION = 5
    private val instance = BooleanArray(MAX_CONCURRENT_SESSION)
    private val sessions = mutableListOf<Pair<SessionDNS, Int>>()
    private var dbUrl: String = ""
    private var taskUrl: String = ""
    private var sManager = MicroServiceManager()

    private fun nextFreeSessionNumber() = instance.indexOfFirst { !it }.also { instance[it] = true }

    fun createNewSession(request: Request, response: Response): String {
        val patId = request.params("patId")

        val instanceId = nextFreeSessionNumber()
        dbUrl = createMicroDatabaseAddress(instanceId)
        taskUrl = createMicroTaskAddress(instanceId)

        println("current boot $instanceId")
        sManager.newSession(instanceId.toString())

        Thread.sleep(BOOT_WAIT_TIME)
        "$dbUrl/api/session/add/$patId/instanceid/$instanceId".httpPost().responseString().third.fold(
            success = {
                val session = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Session>(it)
                        ?: return response.badRequest().also { println("klaxon couldn't parse session") }

                sessions.add(Pair(SessionDNS(session.id, session.cf, taskUrl), instanceId))
            }, failure = { error ->
                if (error.exception.message == "Connection refused (Connection refused)") {
                    return response.resourceNotAvailable(dbUrl)
                }
            sManager.closeSession(instanceId.toString())
                return response.internalServerError(error.exception.message.toString())
            })

        return sessions.last().first.toJson()
    }

    fun closeSessionById(request: Request, response: Response): String {
        val sessionId = request.params("sessionId").toInt()
        val session = sessions.firstOrNull() { it.first.sessionId == sessionId }
        session?: return response.notFound()

        "$dbUrl/api/session/close/$sessionId".httpDelete().responseString().third.fold(
                success = {
                    instance[session.second] = false
                    sessions.removeAll { it.first.sessionId == sessionId }
                    sManager.closeSession(session.second.toString())
                    return response.ok()
                },
            failure = { return it.toJson() }
        )
    }

    fun listAllSessions(request: Request, response: Response): String = GsonInitializer.toJson(sessions.map { x -> x.first })

    private fun buildPort(port: Int, id: Int): Int = port + id

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(Services.TASK_HANDLER.port, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(Services.DATA_BASE.port, id)}"
}