package controller.logic

import com.google.gson.GsonBuilder
import controller.CoreController
import model.Member
import model.PayloadWrapper
import model.SessionOperation
import model.Subscription
import org.eclipse.jetty.websocket.api.Session
import utils.Logger

object SubscriptionHandler {

    fun runOn(core: CoreController) {

        val coreSubject = core.subjects.getSubjectsOf<Pair<Session, String>>(CoreController::class.java.name)!!

        val gson = GsonBuilder().create()

        coreSubject.map { (session, json) ->
            session to gson.fromJson(json, PayloadWrapper::class.java)
        }.subscribe { (session, wrapper) ->
            with(wrapper) {
                when (subject) {
                    SessionOperation.SUBSCRIBE -> {
                        val msg = subject.objectify<Subscription>(body)
                        if (!core.sessions.contains(msg.subject)) {
                            Logger.info("Adding Session for ${msg.subject} @ ${msg.body}")
                            core.sessions[msg.subject] = session
                        }
                        Logger.info("Subscribing ${msg.subject} @ ${msg.body}")
                        core.topics.removeListener(msg.subject)
                        core.topics.add(msg.body, msg.subject)
                    }
                    SessionOperation.CLOSE -> {
                        Logger.info(body)
                        val listener = subject.objectify<Member>(body)
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