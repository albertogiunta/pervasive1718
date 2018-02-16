package controller.logic

import LifeParameters
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.success
import config.Services
import controller.CoreController
import model.Boundary
import model.HealthParameter
import model.PayloadWrapper
import utils.GsonInitializer
import utils.toJson
import java.net.URL

object Notification {

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

        healthParametersURL.httpGet().responseString().third.success {
            healthParameters = GsonInitializer.gson.fromJson(it, Array<HealthParameter>::class.java).map {
                it.id to it
            }.toMap()
        }

        boundaryURL.httpGet().responseString().third.success {
            boundaries = GsonInitializer.gson.fromJson(it, Array<Boundary>::class.java)
                .filter {
                    healthParameters.containsKey(it.healthParameterId)
                }
                .map {
                    LifeParameters.Utils.getByAcronym(healthParameters[it.healthParameterId]!!.acronym) to it
                }.groupBy({it.first}, {it.second})
        }
    }

    fun run(core: CoreController) {

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        // Check OUT OF BOUND Heath Parameters
        with(publishSubjects) {
            this.forEach { topic, subject ->
                subject.map {
                    topic to it.toDouble()
                }.map {(lp, value) ->
                    // Check if out of boundaries and notify of the WS
                    lp to Notification.boundaries[lp]!!
                        .filter {
                            value > it.lowerBound && value < it.lowerBound && !it.itsGood
                        }
                }.filter{ (_, boundaries) ->
                    boundaries.isNotEmpty()
                }.doOnNext {
                    utils.Logger.info(it.toString())
                }.subscribe { (lp, boundaries) ->
                    val message = PayloadWrapper(-1L,
                        model.SessionOperation.NOTIFY,
                        model.Notification(-1L, lp, boundaries).toJson()
                    )
                    // Do Stuff, if necessary but Subscription is MANDATORY.
                    core.topics[topic]?.forEach { member ->
                        core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                    }
                }
            }
        }
    }

}