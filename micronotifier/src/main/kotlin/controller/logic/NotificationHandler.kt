package controller.logic

import com.github.kittinunf.fuel.httpGet
import config.Services
import controller.CoreController
import model.*
import org.eclipse.jetty.websocket.api.WebSocketException
import utils.GsonInitializer
import utils.Logger
import utils.toJson
import java.net.URL

object NotificationHandler {

    private val boundaryURL = URL(
            Services.Utils.Protocols.http,
            Services.Utils.defaultHost,
            Services.DATA_BASE.port,
            "/api/${Boundary::class.simpleName?.toLowerCase()}/all"
    ).toString()

    @Volatile
    private var boundaries: Map<LifeParameters, List<Boundary>> = emptyMap()

    fun runOn(core: CoreController) {

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        // Continuously try to connect to the DB Micro-Service in order to retrieve necessary data
        Thread {
            while (boundaries.isEmpty()) {
                boundaryURL.httpGet().responseString().third.fold(success = {
                    Logger.info("Loaded boundaries from DB")
                    boundaries = GsonInitializer.fromJson(it, Array<Boundary>::class.java)
                            .filter {
                                LifeParameters.values().map { it.id }.contains(it.healthParameterId)
                            }.map {
                                LifeParameters.Utils.getByID(it.healthParameterId) to it
                            }.groupBy({it.first}, {it.second})
                }, failure = {
                    Logger.info("Error Loading boundaries from DB... Retrying")
                })

                Thread.sleep(2000L)
            }
        }.start()

        // Check OUT OF BOUND Heath Parameters
        publishSubjects.forEach { lp, subject ->
            subject.filter{
                boundaries.isNotEmpty()
            }.map {
                it.toDouble()
            }.map {value ->
                // Check if out of boundaries and notify of the WS
                if (boundaries.containsKey(lp)) {
                    boundaries[lp]!!.filter {
                        value >= it.lowerBound - it.lightWarningOffset
                        && value < it.lowerBound + it.lightWarningOffset
                    }.filter { !it.itsGood }
                } else {
                    emptyList()
                }
            }.filter{
                it.isNotEmpty()
            }.map { body ->
                PayloadWrapper(
                    Services.instanceId(),
                    WSOperations.NOTIFY,
                    Notification(lp, body).toJson()
                ).toJson()
            }.doOnNext {
                if (core.useLogging) utils.Logger.info(it.toString())
            }.subscribe {message ->
                // Do Stuff, if necessary but SubscriptionHandler is MANDATORY.
                core.topics[lp]?.forEach { member ->
                    if (core.sessions.contains(member) && core.sessions[member]?.isOpen!!) {
                        try {
                            core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                        } catch (ex : Exception) {
                            when(ex) {
                                is WebSocketException -> {
                                    core.sessions.removeListener(member)
                                    // Can't remove the topics since it's inside their loop
                                }
                                else -> {
                                    Logger.error("Remote Endpoint has been closed...")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
