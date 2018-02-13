@file:Suppress("UNUSED_PARAMETER")

import DefaultPorts.clientSessionPort
import DefaultPorts.dbPort
import DefaultPorts.maxSimultaneousSessions
import DefaultPorts.taskPort
import RestParams.applicationJsonRequestType
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
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
            post("/new/:patId", applicationJsonRequestType) { SessionApi.createNewSession(request, response) }

            // la fa il leader, deve sapere quale chiudere (la prende dalla new)
            delete("/close/:patId", applicationJsonRequestType) { SessionApi.closeSessionById(request, response) }

            // la fanno i membri, e deve restituire la lista di interventi disponibili
            get("/all", applicationJsonRequestType) { SessionApi.listAllSessions(request, response) }

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
                "$dbUrl/api/session/add".httpPost().body(this.toSessionForDB().toJson()).responseString()
            }

            // TODO attach to subset of microservices
            MicroTask.init(buildPort(clientSessionPort, newSessionId), buildPort(taskPort, newSessionId))

            sessions.last().toJson()
        }
    }

    fun closeSessionById(request: Request, response: Response): String {
        val patId = request.params("patId")
        val sessionId = sessions.first { it.patId == patId }

        sessions.removeAll { it.patId == patId }

        "$dbUrl/close/$sessionId".httpDelete().responseString()

        // TODO detach to subset of microservices

        return response.ok()
    }

    fun listAllSessions(request: Request, response: Response): String {
        return sessions.toJson()
    }

    private fun buildPort(port: Int, id: Int): Int = port + (id % maxSimultaneousSessions)

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(taskPort, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(dbPort, id)}"
}