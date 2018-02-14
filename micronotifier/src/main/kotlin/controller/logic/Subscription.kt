package controller.logic

import com.google.gson.GsonBuilder
import controller.CoreController
import model.Member
import model.PayloadWrapper
import model.SessionOperation
import org.eclipse.jetty.websocket.api.Session
import utils.Logger

object Subscription {

    fun run(core: CoreController) {

        val coreSubject = core.subjects.getSubjectsOf<Pair<Session, String>>(CoreController::class.java.name)!!

        val gson = GsonBuilder().create()

        coreSubject.map { (session, json) ->
            session to gson.fromJson(json, PayloadWrapper::class.java)
        }.subscribe { (session, wrapper) ->
            when (wrapper.subject) {

                SessionOperation.SUBSCRIBE -> {
                    val subscription = gson.fromJson(wrapper.body, model.Subscription::class.java)
                    if (!core.sessions.contains(subscription.subject)) {
                        Logger.info("Adding Session for ${subscription.subject} @ ${subscription.body}")
                        core.sessions[subscription.subject] = session
                    }
                    Logger.info("Subscribing ${subscription.subject} @ ${subscription.body}")
                    core.topics.removeListener(subscription.subject)
                    core.topics.add(subscription.body, subscription.subject)
                }
                SessionOperation.CLOSE -> {
                    val listener = gson.fromJson(wrapper.body, Member::class.java)
                    Logger.info("Closing Session for $listener")
                    core.sessions.removeListener(listener)
                    core.topics.removeListener(listener)
                }
                else -> {
                    Logger.info("NOPE...")
                    // Do Nothing at all
                }
            }
        }
    }

}