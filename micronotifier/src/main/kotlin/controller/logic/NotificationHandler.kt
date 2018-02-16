package controller.logic

import LifeParameters
import com.github.kittinunf.fuel.httpGet
import config.Services
import controller.CoreController
import model.Boundary
import model.HealthParameter
import model.PayloadWrapper
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

    private val healthParametersURL = URL(
            Services.Utils.Protocols.http,
            Services.Utils.defaultHost,
            Services.DATA_BASE.port,
            "/api/${HealthParameter::class.simpleName?.toLowerCase()}/all"
    ).toString()

    private lateinit var boundaries: Map<LifeParameters, List<Boundary>>

    init {

        lateinit var healthParameters: Map<Int, HealthParameter>

        healthParametersURL.httpGet().responseString().third.fold(success = {
            healthParameters = GsonInitializer.fromJson(it, Array<HealthParameter>::class.java).map {
                it.id to it
            }.toMap()
        }, failure = {
            healthParameters = emptyMap()
        })

        boundaryURL.httpGet().responseString().third.fold( success = {
            boundaries = GsonInitializer.fromJson(it, Array<Boundary>::class.java)
                    .filter {
                        healthParameters.containsKey(it.healthParameterId)
                    }
                    .map {
                        LifeParameters.Utils.getByAcronym(healthParameters[it.healthParameterId]!!.acronym) to it
                    }.groupBy({it.first}, {it.second})
        }, failure = {
            boundaries = emptyMap()
        })

        Logger.info(boundaries.toString())
    }

    fun runOn(core: CoreController) {

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        if (boundaries.isNotEmpty()) {
            // Check OUT OF BOUND Heath Parameters
            with(publishSubjects) {
                this.forEach { topic, subject ->
                    subject.map {
                        topic to it.toDouble()
                    }.map {(lp, value) ->
                        // Check if out of boundaries and notify of the WS
                        lp to boundaries[lp]?.filter {
                            value > it.lowerBound && value < it.lowerBound && !it.itsGood
                        }
                    }.filter{ (_, boundaries) ->
                        boundaries?.isNotEmpty()!!
                    }.doOnNext {
                        utils.Logger.info(it.toString())
                    }.subscribe { (lp, boundaries) ->
                        val message = PayloadWrapper(-1L,
                                model.SessionOperation.NOTIFY,
                                model.Notification(-1L, lp, boundaries!!).toJson()
                        )
                        // Do Stuff, if necessary but SubscriptionHandler is MANDATORY.
                        core.topics[topic]?.forEach { member ->
                            if (core.sessions[member]?.isOpen!!) {
                                core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                            }
                        }
                    }
                }
            }
        }
    }

}