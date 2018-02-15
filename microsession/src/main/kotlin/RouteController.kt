@file:Suppress("UNUSED_PARAMETER")

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import config.Services
import config.Services.Utils
import spark.Request
import spark.Response
import spark.Spark.path
import spark.Spark.port
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post

object RouteController {

    fun initRoutes(localPort: Int) {

        port(localPort)

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

    private val boots = BooleanArray(5)
    private val sessions = mutableListOf<Pair<SessionDNS, Int>>()
    private var dbUrl: String = ""
    private var taskUrl: String = ""

    private fun nextFreeSessionNumber() = boots.indexOfFirst { !it }.also { boots[it] = true }

    fun createNewSession(request: Request, response: Response): String {
        val patId = request.params("patId")

        val currentBoot = nextFreeSessionNumber()
        dbUrl = createMicroDatabaseAddress(currentBoot)
        taskUrl = createMicroTaskAddress(currentBoot)


        // TODO attach to subset of microservices

        "$dbUrl/api/session/add/$patId/instanceid/$currentBoot".httpPost().responseString().third.fold(
            success = {
                val session = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Session>(it)
                        ?: return response.badRequest().also { println("klaxon couldn't parse session") }
                sessions.add(Pair(SessionDNS(session.id, session.cf, taskUrl), currentBoot))
            }, failure = { error ->
                if (error.exception.message == "Connection refused (Connection refused)") {
                    return response.hostNotFound(dbUrl)
                }
                println(error)
                return response.badRequest()
            })

        return sessions.last().first.toJson()
    }

    fun closeSessionById(request: Request, response: Response): String {
        val sessionId = request.params("sessionId").toInt()
        val session = sessions.first { it.first.sessionId == sessionId }

        boots[session.second] = false

        sessions.removeAll { it.first.sessionId == sessionId }

        // TODO detach to subset of microservices

        "$dbUrl/api/session/close/$sessionId".httpDelete().responseString().third.fold(
            success = { return response.ok() },
            failure = { return it.toJson() }
        )
    }

    fun listAllSessions(request: Request, response: Response): String = GsonInitializer.toJson(sessions)

    private fun buildPort(port: Int, id: Int): Int = port + id

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(Services.TASK_HANDLER.port, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(Services.DATA_BASE.port, id)}"
}