package controller.logic

import com.google.gson.GsonBuilder
import controller.CoreController
import model.PayloadWrapper
import toJson

object Notification {

    fun run(core : CoreController) {
        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        val gson = GsonBuilder().create()

        // Check OUT OF BOUND Heath Parameters
        with(publishSubjects) {
            this.forEach { topic, subject ->
                subject.map {
                    topic to it.toDouble()
                }.filter {
                    // Check if out of boundaries and notify of the WS
                    false
                }.doOnNext {
                    utils.Logger.info(it.toString())
                }.subscribe { (lp, value) ->
                    val message = PayloadWrapper(-1L,
                            model.SessionOperation.NOTIFY,
                            model.Notification(-1L, setOf(lp), "...").toJson()
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