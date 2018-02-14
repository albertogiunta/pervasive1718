package controller.logic

import com.google.gson.GsonBuilder
import controller.CoreController
import model.PayloadWrapper
import toJson

object Relay {

    fun run(core: CoreController) {
        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        val gson = GsonBuilder().create()

        // Simple relays received Health Values to Listeners
        with(publishSubjects) {
            this.forEach { topic, subject ->
                subject.map {
                    topic to it.toDouble()
                }.doOnNext {
                    utils.Logger.info(it.toString())
                }.subscribe { (lp, value) ->
                    val message = PayloadWrapper(-1L,
                            model.SessionOperation.UPDATE,
                            model.Update(-1L, lp, value).toJson()
                    )
                    // Do stuff with the WebSockets, dispatch only some of the merged values
                    // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
                    core.topics[topic]?.forEach { member ->
                        utils.Logger.info("$member ===> ${message.toJson()}")
                        core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                    }
                }
            }
        }
    }
}