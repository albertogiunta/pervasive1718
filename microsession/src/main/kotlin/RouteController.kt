@file:Suppress("UNUSED_PARAMETER")

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
            delete("/close/:patId", Utils.RESTParams.applicationJson) { SessionApi.closeSessionById(request, response) }

            // la fanno i membri, e deve restituire la lista di interventi disponibili
            get("/all", Utils.RESTParams.applicationJson) { SessionApi.listAllSessions(request, response) }

        }
    }
}


object SessionApi {

    private val sessions = mutableListOf<SessionDNS>()
    private lateinit var dbUrl: String
    private lateinit var taskUrl: String

    fun createNewSession(request: Request, response: Response): String {
        val patId = request.params("patId")
        val newSessionId = if (sessions.isEmpty()) 0 else sessions.last().sessionId + 1

        return if (sessions.find { it.patId == patId } != null) {
            response.badRequest()
        } else {
            with(newSessionId) {
                dbUrl = createMicroDatabaseAddress(this)
                taskUrl = createMicroTaskAddress(this)
            }

            with(SessionDNS(newSessionId, patId, taskUrl)) {
                sessions.add(this)
                "$dbUrl/api/session/add".httpPost().body(GsonInitializer.toJson(this.toSessionForDB())).responseString()
            }

            // TODO attach to subset of microservices
            GsonInitializer.toJson(sessions.last())
        }
    }

    fun closeSessionById(request: Request, response: Response): String {
        val patId = request.params("patId")
        val session = sessions.first { it.patId == patId }
        val sessionId = session.sessionId

        sessions.removeAll { it.patId == patId }

        "$dbUrl/api/session/close/$sessionId".httpDelete().responseString()

        // TODO detach to subset of microservices

        return response.ok()
    }

    fun listAllSessions(request: Request, response: Response): String {
        return GsonInitializer.toJson(sessions)
    }

    private fun buildPort(port: Int, id: Int): Int = port + (id % Utils.maxSimultaneousSessions)

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(Services.TASK_HANDLER.port, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(Services.DATA_BASE.port, id)}"
}