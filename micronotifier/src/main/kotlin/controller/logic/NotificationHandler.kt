package controller.logic

import com.github.kittinunf.fuel.httpGet
import config.Services
import controller.CoreController
import model.*
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

    private lateinit var boundaries: Map<LifeParameters, List<Boundary>>

    fun runOn(core: CoreController) {

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        boundaryURL.httpGet().responseString().third.fold( success = {
            Logger.info("Loaded boundaries from DB")
            boundaries = GsonInitializer.fromJson(it, Array<Boundary>::class.java)
                    .filter {
                        LifeParameters.values().map { it.id }.contains(it.healthParameterId)
                    }.map {
                        LifeParameters.Utils.getByID(it.healthParameterId) to it
                    }.groupBy({it.first}, {it.second})
        }, failure = {
            Logger.info("Error Loading boundaries from DB")
            boundaries = emptyMap()
        })

        if (boundaries.isNotEmpty()) {
            // Check OUT OF BOUND Heath Parameters
            publishSubjects.forEach { lp, subject ->
                subject.map {
                    it.toDouble()
                }.map {value ->
                    // Check if out of boundaries and notify of the WS
                    if (boundaries.containsKey(lp)) {
                        boundaries[lp]!!.filter {
                            value >= it.lowerBound - it.lightWarningOffset
                            && value < it.lowerBound + it.lightWarningOffset
//                            && !it.itsGood
                        }.filter { !it.itsGood }
                    } else {
                        emptyList()
                    }
                }.filter{
                    it.isNotEmpty()
                }.map { body ->
                    PayloadWrapper(-1L,
                            WSOperations.NOTIFY,
                            Notification(lp, body).toJson()
                    ).toJson()
                }.doOnNext {
                    //utils.Logger.info(it.toString())
                }.subscribe {message ->
                    // Do Stuff, if necessary but SubscriptionHandler is MANDATORY.
                    core.topics[lp]?.forEach { member ->
                        if (core.sessions[member]?.isOpen!!) {
                            core.sessions[member]?.remote?.sendString(message) // Notify the WS, dunno how.
                        }
                    }
                }
            }
        }
    }
}
