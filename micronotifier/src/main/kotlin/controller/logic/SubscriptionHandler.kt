package controller.logic

import config.Services
import controller.CoreController
import io.reactivex.subjects.Subject
import model.*
import networking.ws.NotifierReferenceManager
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session
import utils.GsonInitializer
import utils.Logger
import utils.toJson

object SubscriptionHandler {

    fun runOn(core: CoreController) {

        val wsRef : RelayService? = NotifierReferenceManager[RelayService::class.java.name]
        val wsSubject: Subject<Pair<Session, String>> = core.subjects.getSubjectsOf(RelayService::class.java.name)!!

        wsSubject.map { (session, json) ->
            session to GsonInitializer.fromJson(json, PayloadWrapper::class.java)
        }.subscribe { (session, wrapper) ->
            with(wrapper) {
                when (subject) {
                    WSOperations.SUBSCRIBE -> {
                        val msg: Subscription = wrapper.objectify(body)
                        Logger.info("Adding Session for ${msg.subject} @ ${msg.topics}")
                        core.sessions[msg.subject] = session
                        Logger.info("Subscribing ${msg.subject} @ ${msg.topics}")
                        core.topics.removeListener(msg.subject)
                        core.topics.add(msg.topics, msg.subject)

                        val okResponse = PayloadWrapper(
                                Services.instanceId(),
                                WSOperations.ANSWER,
                                Response(200, wrapper.toJson()).toJson()
                        )

                        wsRef?.sendMessage(session, okResponse)

                    }
                    WSOperations.CLOSE -> {
                        val listener: Member = wrapper.objectify(body)
                        Logger.info("Closing Session for $listener")
                        core.sessions.removeListener(listener)
                        core.topics.removeListener(listener)

                        val okResponse = PayloadWrapper(
                                Services.instanceId(),
                                WSOperations.ANSWER,
                                Response(200, wrapper.toJson()).toJson()
                        )

                        try {
                            wsRef?.sendMessage(session, okResponse)
                        } catch (ex : Exception) {
                            Logger.error("Remote Endpoint has been closed...")
                        }
                    }
                    else -> {
                        Logger.info(this.toString())
                        // Do Nothing at all
                    }
                }
            }
        }

        Logger.info("SubscriptionHandler Loaded... @${wsRef?.name}")
    }
}