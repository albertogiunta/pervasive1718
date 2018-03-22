package controller.logic

import Params
import WSLogger
import com.github.kittinunf.fuel.httpGet
import config.Services
import controller.CoreController
import model.*
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.WebSocketException
import utils.GsonInitializer
import utils.Logger
import utils.toJson
import java.net.URL

/**
 * @author XanderC
 *
 */
object NotificationHandler {

    private val boundaryURL = URL(
            Services.Utils.Protocols.http,
            Services.Utils.defaultHost,
            Services.DATA_BASE.port,
            "/api/${Params.Boundary.API_NAME}"
    ).toString()

    @Volatile
    private var boundaries: Map<LifeParameters, List<Boundary>> = emptyMap()

    init { }

    fun runOn(core: CoreController) {

        // Continuously try to connect to the DB Micro-Service in order to retrieve necessary data
        Thread {

            Logger.info("Connecting to $boundaryURL")

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
                    Logger.info("Error Loading boundaries from DB... Retrying connecting to $boundaryURL")
                })

                Thread.sleep(1000L)
            }
        }.start()

        val publishSubjects = core.topics.activeTopics().map {
            it to core.sources.getObservableSourceOf<String>(it.toString())!!
        }.toMap()

        // Check OUT OF BOUND Heath Parameters
        publishSubjects.forEach { lifeParameter, subject ->
            subject.filter{
                boundaries.isNotEmpty()
            }.map {
                it.toDouble()
            }.map {value ->
                // Check if out of boundaries and notify of the WS
                if (boundaries.containsKey(lifeParameter)) {
                    boundaries[lifeParameter]!!.filter {
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
                    Notification(lifeParameter, body).toJson()
                )
            }.doOnNext {
                if (core.useLogging) utils.Logger.info(it.toString())
            }.subscribe {message ->
                // Do Stuff, if necessary but SubscriptionHandler is MANDATORY.
                core.topics[lifeParameter]?.forEach { member ->
                    if (core.sessions.contains(member)) {
                        try {
                            RelayService.sendMessage(WSLogger.WSUser.SERVER, config.Services.NOTIFIER.wsPath, core.sessions[member]!!, message)
                        } catch (ex : Exception) {
                            when(ex) {
                                is WebSocketException -> {
                                    core.sessions.removeListener(member)
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

        Logger.info("NotificationHandler Loaded...")
    }
}
