import Const.clientSessionPort
import Const.dbPort
import Const.maxSimultaneousSessions
import Const.taskPort
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import spark.Request
import spark.Response
import spark.RouteGroup
import spark.Spark.path
import spark.Spark.port
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post

interface Controller {

    // TODO metti in commons insieme a quello di microdb
    companion object {
        const val applicationJsonRequestType = "application/json"
    }

}

object RouteController {

    fun routes(localPort: Int): RouteGroup {

//        port(localPort)

        return RouteGroup {

            path("/session") {

                // la fa il leader, ritorna il riferimento di [MT], crea una websocket per mandargli i membri mano a mano che arrivano, ti ritorna anche l'id della sessione
                post("/new/:patId", Controller.applicationJsonRequestType) { SessionApi.createNewSession(request, response) }

                // la fa il leader, deve sapere quale chiudere (la prende dalla new)
                delete("/close/:patId", Controller.applicationJsonRequestType) { SessionApi.closeSessionById(request, response) }

                // la fanno i membri, e deve restituire la lista di interventi disponibili
                get("/all", Controller.applicationJsonRequestType) { SessionApi.listAllSessions(request, response) }
            }

        }
    }

    fun initRoutes(localPort: Int) {

        port(localPort)

        path("/session") {

            // la fa il leader, ritorna il riferimento di [MT], crea una websocket per mandargli i membri mano a mano che arrivano, ti ritorna anche l'id della sessione
            post("/new/:patId", Controller.applicationJsonRequestType) { SessionApi.createNewSession(request, response) }

            // la fa il leader, deve sapere quale chiudere (la prende dalla new)
            delete("/close/:patId", Controller.applicationJsonRequestType) { SessionApi.closeSessionById(request, response) }

            // la fanno i membri, e deve restituire la lista di interventi disponibili
            get("/all", Controller.applicationJsonRequestType) { SessionApi.listAllSessions(request, response) }

        }
    }
}

data class SessionDNS(val sessionId: Int, val patId: String, val microTaskAddress: String)

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

            val newSession = SessionDNS(newSessionId, patId, taskUrl)
            sessions.add(newSession)

            Thread({
                MicroDatabase.init(buildPort(dbPort, newSessionId))
                MicroTask.init(buildPort(clientSessionPort, newSessionId), buildPort(taskPort, newSessionId))
                Thread.sleep(5000)
                "$dbUrl/api/session/add".httpPost().body(newSession.toString()).responseString()
                println("finito dentro thread")
            }).start()

            sessions.last().toJson()
        }
    }

    fun closeSessionById(request: Request, response: Response): String {
        val patId = request.params("patId")
        val sessionId = sessions.first { it.patId == patId }

        "$dbUrl/close/$sessionId".httpDelete().responseString()
        sessions.removeAll { it.patId == patId }

        return response.ok()
    }

    fun listAllSessions(request: Request, response: Response): String {
        return sessions.toJson()
    }

    private fun buildPort(port: Int, id: Int): Int = port + (id % maxSimultaneousSessions)

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(taskPort, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(dbPort, id)}"
}