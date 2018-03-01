package controller.logic

import config.Services
import controller.CoreController
import model.PayloadWrapper
import model.Update
import model.WSOperations
import org.eclipse.jetty.websocket.api.WebSocketException
import utils.Logger
import utils.toJson

object RelayHandler {

    fun runOn(core: CoreController) {

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        // Simple relays received Health Values to Listeners
        with(publishSubjects) {
            this.forEach { topic, subject ->
                subject.map {
                    topic to it.toDouble()
                }.doOnNext {
                    if (core.useLogging) Logger.info(it.toString())
                }.subscribe { (lp, value) ->
                    val message = PayloadWrapper(
                            Services.instanceId(),
                            WSOperations.UPDATE,
                            Update(lp, value).toJson()
                    )
                    // Do stuff with the WebSockets, dispatch only some of the merged values
                    // With one are specified into controller.listenerMap: Member -> Set<model.LifeParameters>
                    core.topics[topic]?.forEach { member ->
                        if (core.sessions.contains(member) && core.sessions[member]?.isOpen!!) {
                            try {
                                core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
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
        }
    }
}