@file:Suppress("UNUSED_PARAMETER")

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import config.Services
import config.Services.Utils
import model.*
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import process.MicroServiceManager
import spark.Request
import spark.Response
import spark.Spark.path
import spark.Spark.port
import spark.kotlin.delete
import spark.kotlin.get
import utils.GsonInitializer
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson

object RouteController {

    fun initRoutes() {

        port(Services.SESSION.port)

        path("/session") {
            // la fa il leader, deve sapere quale chiudere (la prende dalla new)
            delete("/close/:sessionId", Utils.RESTParams.applicationJson) { SessionApi.closeSessionById(request, response) }

            // la fanno i membri, e deve restituire la lista di interventi disponibili
            get("/all", Utils.RESTParams.applicationJson) { SessionApi.listAllSessions(request, response) }

            // la fa il leader e restituisce la lista dei suoi interventi aperti
            get("/all/:leaderId", Utils.RESTParams.applicationJson) { SessionApi.listAllOpenSessionsByLeaderId(request, response) }

            // la fanno i vari microservices per notificare il
            get("/acknowledge/:instanceId", Utils.RESTParams.applicationJson) { SessionApi.acknowledgeReadyService(request, response) }
        }
    }
}


object SessionApi {

    private val instance = BooleanArray(Utils.maxSimultaneousSessions)
    private val sessions = mutableListOf<Pair<SessionDNS, Int>>()
    private var sManager = MicroServiceManager()
    private var serviceInitializationStatus = HashMap<Int,Int>()
    private var sessionInitializationParamsWithInstanceId = HashMap<Int, Pair<SessionAssignment, Session>>()
    private var ws = WSSessionServer()

    private fun nextFreeSessionNumber() = instance.indexOfFirst { !it }.also { instance[it] = true }

    fun closeSessionById(request: Request, response: Response): String {
        val sessionId = request.params("sessionId").toInt()
        val session = sessions.firstOrNull { it.first.sessionId == sessionId }
        session?: return response.notFound()

        val dbUrl = createMicroDatabaseAddress(session.second)

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

    fun listAllOpenSessionsByLeaderId(request: Request, response: Response): String {
        val leaderId = request.params("leaderId").toInt()
        sessionInitializationParamsWithInstanceId.forEach {
            if (it.value.first.leaderId == leaderId)
                return sessions.filter { session -> session.second == it.key }.map{ x -> x.first}.toJson()
        }
        return emptyList<SessionDNS>().toJson()
    }

    fun acknowledgeReadyService(request: Request, response: Response): String {
        val instanceId = request.params("instanceId").toInt()
        serviceInitializationStatus[instanceId] = serviceInitializationStatus[instanceId]!! +1
        if (serviceInitializationStatus[instanceId] == 4) {
            val dbUrl = createMicroDatabaseAddress(instanceId)

            val instanceDetails = sessionInitializationParamsWithInstanceId[instanceId]!!

            "$dbUrl/api/session/add/${instanceDetails.first.patId}/instanceid/$instanceId/leaderid/${instanceDetails.first.leaderId}".httpPost().responseString().third.fold(
                    success = {
                        val session = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<model.Session>(it)
                                ?: ws.sendMessage(instanceDetails.second, PayloadWrapper(-1, WSOperations.SESSION_HANDLER_ERROR_RESPONSE, "Cannot parse response from database - session not created"))
                        session as model.Session
                        sessions.add(Pair(SessionDNS(session.id, session.cf, instanceId), instanceId))
                        ws.sendMessage(instanceDetails.second, PayloadWrapper(-1, WSOperations.SESSION_HANDLER_RESPONSE, SessionDNS(session.id, session.cf, instanceId).toJson()))
                    }, failure = { error ->
                        if (error.exception.message == "Connection refused (Connection refused)") {
                            ws.sendMessage(instanceDetails.second, PayloadWrapper(-1, WSOperations.SESSION_HANDLER_ERROR_RESPONSE, "Connection refused - session not created"))
                        }
                        sManager.closeSession(instanceId.toString())
                        ws.sendMessage(instanceDetails.second, PayloadWrapper(-1, WSOperations.SESSION_HANDLER_ERROR_RESPONSE, "Internal server error - session not created"))
                    })
            }

        // So senders can have a response
        return response.ok()
    }

    fun listAllSessions(request: Request, response: Response): String = GsonInitializer.toJson(sessions.map { x -> x.first })

    private fun buildPort(port: Int, id: Int): Int = port + id

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(Services.TASK_HANDLER.port, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(Services.DATA_BASE.port, id)}"

    @Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
    @WebSocket
    class WSSessionServer : WSServer<PayloadWrapper>() {

        override fun onMessage(session: Session, message: String) {
            super.onMessage(session, message)
            print(message)
            val taskWrapper = Serializer.klaxon.fieldConverter(KlaxonDate::class, dateConverter).parse<PayloadWrapper>(message)
            taskWrapper?.let {
                with(taskWrapper) {
                    when (subject) {
                        WSOperations.NEW_SESSION -> {
                            val instanceId = nextFreeSessionNumber()
                            serviceInitializationStatus[instanceId] = 0
                            sessionInitializationParamsWithInstanceId[instanceId] = Pair(taskWrapper.objectify(body), session)

                            println("current boot $instanceId")
                            sManager.newSession(instanceId.toString())
                        } // done by leader
                        else -> println("Message was not handled " + message)
                    }
                }
            }
        }
    }
}