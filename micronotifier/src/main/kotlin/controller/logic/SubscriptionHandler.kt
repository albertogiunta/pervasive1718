package controller.logic

import config.Services
import controller.CoreController
import model.*
import org.eclipse.jetty.websocket.api.Session
import utils.GsonInitializer
import utils.Logger
import utils.toJson

object SubscriptionHandler {

    fun runOn(core: CoreController) {

        val coreSubject = core.subjects.getSubjectsOf<Pair<Session, String>>(CoreController::class.java.name)!!

        coreSubject.map { (session, json) ->
            Logger.info(json)
            session to GsonInitializer.fromJson(json, PayloadWrapper::class.java)
        }.subscribe { (session, wrapper) ->
            with(wrapper) {
                when (subject) {
                    WSOperations.SUBSCRIBE -> {
                        val msg: Subscription = wrapper.objectify(body)
                        if (!core.sessions.contains(msg.subject)) {
                            Logger.info("Adding Session for ${msg.subject} @ ${msg.topics}")
                            core.sessions[msg.subject] = session
                        }
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
                        Logger.info(body)
                        val listener: Member = wrapper.objectify(body)
                        Logger.info("Closing Session for $listener")
                        core.sessions.removeListener(listener)
                        core.topics.removeListener(listener)
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