package networking.ws

import LifeParameters
import WSParams
import WSServer
import WSServerInitializer
import com.google.gson.GsonBuilder
import controller.CoreController
import controller.NotifierTopicsController
import model.Payload
import model.PayloadWrapper
import model.SessionOperation
import model.Subscription
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.Logger

@WebSocket
class RelayService : WSServer<Payload<SessionOperation, String>>() {

    private val core = CoreController.singleton()

    private val gson = GsonBuilder().create()

    init {
        Logger.info(core.topics.activeTopics().toString())
    }

    override fun closed(session: Session, statusCode: Int, reason: String) {
        super.closed(session, statusCode, reason)
        core.sessions.removeListenerOn(session)
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)

        val request = gson.fromJson(message, PayloadWrapper::class.java)

        // Probably this should be moved into the controller => Pattern patterns.Observer
        // If a Pattern patterns.Observer is used then both the controllers should be wrapped by a core controller
        // Where the whole observation behavior is handled
        with(request) {

            when (subject) {
                SessionOperation.OPEN -> core.sessions.open(sid)
                SessionOperation.CLOSE -> core.sessions.closeSession(sid)
                SessionOperation.ADD -> {
                    val subscription = gson.fromJson(request.body, Subscription::class.java)
                    core.sessions[subscription.subject] = session
                    core.topics.add(subscription.body, subscription.subject)
                }
                SessionOperation.REMOVE -> {
                    val subscription = gson.fromJson(request.body, Subscription::class.java)
                    core.topics.removeListener(subscription.subject)
                }
                SessionOperation.SUBSCRIBE -> {
                    val subscription = gson.fromJson(request.body, Subscription::class.java)
                    core.topics.removeListener(subscription.subject)
                    core.topics.add(subscription.body, subscription.subject)
                }
                SessionOperation.UNSUBSCRIBE -> {
                    val subscription = gson.fromJson(request.body, Subscription::class.java)
                    core.topics.removeListenerOn(subscription.body, subscription.subject)
                }

                else -> {
                } // Do Nothing at all
            }
        }
    }
}

fun main(args: Array<String>) {

    NotifierTopicsController.init(LifeParameters.values().toSet())
    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)
}