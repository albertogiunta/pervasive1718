import spark.Request
import spark.Response
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

    fun initRoutes()

}

object RouteController : Controller {

    override fun initRoutes() {

        path("/session") {

            // la fa il leader, ritorna il riferimento di [MT], crea una websocket per mandargli i membri mano a mano che arrivano, ti ritorna anche l'id della sessione
            post("/new/:roomId", Controller.applicationJsonRequestType) { SessionApi.createNewSession(request, response) }

            // la fa il leader, deve sapere quale chiudere (la prende dalla new)
            delete("/close/:roomId", Controller.applicationJsonRequestType) { SessionApi.closeSessionById(request, response) }

            // la fanno i membri, e deve restituire la lista di interventi disponibili
            get("/all", Controller.applicationJsonRequestType) { SessionApi.listAllSessions(request, response) }

        }

    }
}

data class Session(val sessionId: Int, val roomId: Int, val microTaskAddress: String)

object SessionApi {

    private const val basePort = 8080
    private val sessions = mutableListOf<Session>()

    fun createNewSession(request: Request, response: Response): String {
        val newSessionId = if (sessions.isEmpty()) 0 else sessions.last().sessionId + 1
        val roomId = request.params("roomId").toInt()

        return if (sessions.find { it.roomId == roomId } != null) {
            response.badRequest()
        } else {
            sessions.add(Session(newSessionId, roomId, createMicroTaskAddress(roomId)))
            sessions.last().toJson()
        }
    }

    fun closeSessionById(request: Request, response: Response): String {
        val roomId = request.params("roomId").toInt()
        sessions.removeAll { it.roomId == roomId }
        return response.ok()
    }

    fun listAllSessions(request: Request, response: Response): String {
        return sessions.toJson()
    }

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${basePort + id}/task"
}

fun main(args: Array<String>) {
    port(8000)
    RouteController.initRoutes()
}
