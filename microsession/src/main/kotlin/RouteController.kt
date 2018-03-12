@file:Suppress("UNUSED_PARAMETER")

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
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
import utils.*
import java.sql.Timestamp
import java.util.*

object RouteController {

    fun initRoutes() {

        port(Services.SESSION.port)

        path("/${Params.Session.API_NAME}") {
            get("", Utils.RESTParams.applicationJson) { SessionApi.listAllSessions(request, response) }
            get("/:leadercf", Utils.RESTParams.applicationJson) { SessionApi.listAllOpenSessionsByLeaderCF(request, response) }
            get("/acknowledge/:instanceid", Utils.RESTParams.applicationJson) { SessionApi.acknowledgeReadyService(request, response) }
            delete("/:sessionid", Utils.RESTParams.applicationJson) { SessionApi.closeSessionById(request, response) }
        }
    }
}


object SessionApi {

    private val instance = BooleanArray(Utils.maxSimultaneousSessions)
    private val sessions = mutableListOf<Pair<SessionDNS, Int>>()
    private var sManager = MicroServiceManager()
    private var serviceInitializationStatus = HashMap<Int, Int>()
    private var sessionInitializationParamsWithInstanceId = HashMap<Int, Pair<SessionAssignment, Session>>()
    private var ws = WSSessionServer()

    @Throws(IndexOutOfBoundsException::class)
    private fun nextFreeSessionNumber() = instance.indexOfFirst { !it }.also { instance[it] = true }

    fun closeSessionById(request: Request, response: Response): String {
        val sessionId = request.params("sessionid").toInt()
        val session = sessions.firstOrNull { it.first.sessionId == sessionId }
        session ?: return response.notFound("Non existing session requested.")

        val dbUrl = createMicroDatabaseAddress(session.second)

        "$dbUrl/${Connection.API}/${Params.Session.API_NAME}/$sessionId".httpPut().responseString().third.fold(
            success = {
                instance[session.second] = false
                ReportGenerator.generateFinalReport(sessionId.toString())
                sessions.removeAll { it.first.sessionId == sessionId }
                sManager.closeSession(session.second.toString())
                return response.ok()
            },
            failure = { return response.resourceNotAvailable(dbUrl, it.toJson()) }
        )
    }

    fun listAllOpenSessionsByLeaderCF(request: Request, response: Response): String {
        val leaderCF = request.params("leadercf")
        return sessions.filter { it.first.leaderCF == leaderCF }.map { it.first }.toJson()
    }

    fun acknowledgeReadyService(request: Request, response: Response): String {
        val instanceId = request.params("instanceid").toInt()
        println("[RECEIVED ACK FOR INSTANCE $instanceId, map is $serviceInitializationStatus")
        serviceInitializationStatus[instanceId] = serviceInitializationStatus[instanceId]?.plus(1) ?:
                return response.badRequest("")
        println("INSTANCE ACKS ARE ${serviceInitializationStatus[instanceId]}")
        if (serviceInitializationStatus[instanceId] == 4) {
            val dbUrl = createMicroDatabaseAddress(instanceId)
            val visorUrl = Services.Utils.defaultHostUrlApi(Services.VISORS)

            val instanceDetails = sessionInitializationParamsWithInstanceId[instanceId]!!

            "$dbUrl/${Connection.API}/${Params.Session.API_NAME}".httpPost().body(Session(-1, instanceDetails.first.patientCF, instanceDetails.first.leaderCF, Timestamp(Date().time), null, instanceId).toJson()).responseString().third.fold(
                success = {
                    val session = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<model.Session>(it)
                    if (session != null) {
                        sessions.add(Pair(SessionDNS(session.id, session.patientCF, instanceId, session.leaderCF), instanceId))
                        ws.sendMessage(instanceDetails.second, PayloadWrapper(-1, WSOperations.SESSION_HANDLER_RESPONSE, SessionDNS(session.id, session.patientCF, instanceId, session.leaderCF).toJson()))
                        "$visorUrl/${Params.Session.API_NAME}".httpPost().body(SessionInfo(session.patientCF).toJson()).responseString()
                    } else {
                        ws.sendMessage(instanceDetails.second, PayloadWrapper(-1, WSOperations.SESSION_HANDLER_ERROR_RESPONSE, "Cannot parse response from database - session not created"))
                    }
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

    fun listAllSessions(request: Request, response: Response): String =
        GsonInitializer.toJson(sessions.map { it.first })

    private fun buildPort(port: Int, id: Int): Int = port + id

    private fun createMicroTaskAddress(id: Int) = "http://localhost:${buildPort(Services.TASK_HANDLER.port, id)}"

    private fun createMicroDatabaseAddress(id: Int) = "http://localhost:${buildPort(Services.DATA_BASE.port, id)}"

    @Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
    @WebSocket
    class WSSessionServer : WSServer<PayloadWrapper>("Session") {

        override fun onMessage(session: Session, message: String) {
            super.onMessage(session, message)
            val sessionWrapper = Serializer.klaxon.fieldConverter(KlaxonDate::class, dateConverter).parse<PayloadWrapper>(message)
            sessionWrapper?.let {
                with(sessionWrapper) {
                    when (subject) {
                        WSOperations.NEW_SESSION -> {
                            try {
                                val instanceId = nextFreeSessionNumber()

                                serviceInitializationStatus[instanceId] = 0
                                sessionInitializationParamsWithInstanceId[instanceId] = Pair(sessionWrapper.objectify(body), session)
                                println("Current boot $instanceId")
                                println("Last session ${sessionInitializationParamsWithInstanceId[instanceId]!!.first}")
                                sManager.newSession(instanceId.toString())

                            } catch (ex : Exception) {
                                when(ex) {
                                    is IndexOutOfBoundsException -> {

                                        println("[Error] Creating a new instance is impossible. Max Simultaneous Instances ${Utils.maxSimultaneousSessions}")

                                        val errorMessage = PayloadWrapper(
                                                Services.instanceId(),
                                                WSOperations.ERROR_CREATING_INSTANCE_POOL_FULL,
                                                Unit.toJson()
                                        )

                                        ws.sendMessage(session, errorMessage)
                                    }
                                    else -> {
                                        ex.printStackTrace()
                                    }
                                }
                            }
                        } // done by leader
                        else -> println("Message was not handled " + message)
                    }
                }
            }
        }
    }
}