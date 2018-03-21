package controller.logic

import WSLogger
import config.Services
import controller.CoreController
import model.PayloadWrapper
import model.Update
import model.WSOperations
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.WebSocketException
import utils.Logger
import utils.toJson

object RelayHandler {

    fun runOn(core: CoreController) {

        val publishSubjects = core.topics.activeTopics().map {
            it to core.sources.getObservableSourceOf<String>(it.toString())!!
        }.toMap()

        // Simple relays received Health Values to Listeners
        publishSubjects.forEach { lifeParameter, source ->
            source.map { value ->
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

        Logger.info("RelayHandler Loaded...")
    }
}