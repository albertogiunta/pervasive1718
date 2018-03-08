package controller.logic

import config.Services
import controller.CoreController
import model.PayloadWrapper
import model.Update
import model.WSOperations
import networking.ws.NotifierReferenceManager
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.WebSocketException
import utils.Logger
import utils.toJson

object RelayHandler {

    fun runOn(core: CoreController) {

        val wsRef : RelayService? = NotifierReferenceManager[RelayService::class.java.name]

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.getSubjectsOf<String>(it.toString())!!
        }.toMap()

        // Simple relays received Health Values to Listeners
        with(publishSubjects) {
            this.forEach { lifeParameter, subject ->
                subject.map { value ->
                    PayloadWrapper(
                            Services.instanceId(),
                            WSOperations.UPDATE,
                            Update(lifeParameter, value.toDouble()).toJson()
                    )
                }.doOnNext {
                    if (core.useLogging) Logger.info(it.toString())
                }.subscribe { message ->
                    // Do stuff with the WebSockets, dispatch only some of the merged values
                    // With one are specified into controller.listenerMap: Member -> Set<model.LifeParameters>
                    core.topics[lifeParameter]?.forEach { member ->
                        if (core.sessions.contains(member)) {
                            try {
                                wsRef?.sendMessage(core.sessions[member]!!, message)
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

        Logger.info("RelayHandler Loaded... @${wsRef?.name}")
    }
}