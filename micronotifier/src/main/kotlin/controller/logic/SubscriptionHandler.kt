package controller.logic

import config.Services
import controller.CoreController
import model.*
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session
import utils.GsonInitializer
import utils.Logger
import utils.toJson

object SubscriptionHandler {

    fun runOn(core: CoreController) {

        val wsSubject = core.subjects.getSubjectsOf<Pair<Session, String>>(RelayService::class.java.name)!!

        wsSubject.map { (session, json) ->
            Logger.info(json)
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

                        session.remote.sendString(okResponse.toJson())

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

                        if (session.isOpen) {
                            try {
                                session.remote.sendString(okResponse.toJson())
                            } catch (ex : Exception) {
                                Logger.error("Remote Endpoint has been closed...")
                            }
                        }

                    }
                    else -> {
                        Logger.info(this.toString())
                        // Do Nothing at all
                    }
                }
            }
        }
    }
}