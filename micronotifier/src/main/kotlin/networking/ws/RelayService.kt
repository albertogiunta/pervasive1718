package networking.ws

import LifeParameters
import WSParams
import WSServer
import WSServerInitializer
import com.google.gson.GsonBuilder
import controller.NotifierSessionsController
import controller.NotifierTopicsController
import controller.SessionsController
import controller.TopicsController
import logic.Member
import model.Payload
import model.PayloadWrapper
import model.SessionOperation
import model.Subscription
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.Logger

@WebSocket
class RelayService : WSServer<Payload<SessionOperation, String>>() {

    val topicsController: () -> TopicsController<LifeParameters, Member> = { NotifierTopicsController.singleton() }
    val sessionsController: () -> SessionsController<Member, Session> = { NotifierSessionsController.singleton() }
    val Gson = GsonBuilder().create()

    init {
        Logger.info(topicsController().activeTopics().toString())
    }

    override fun closed(session: Session, statusCode: Int, reason: String) {
        super.closed(session, statusCode, reason)
        sessionsController().removeListenerOn(session)
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)

        val request = Gson.fromJson(message, PayloadWrapper::class.java)

        // Probably this should be moved into the controller => Pattern Observer
        // If a Pattern Observer is used then both the controllers should be wrapped by a main controller
        // Where the whole observation behavior is handled
        with(request) {

            when (topic) {
                SessionOperation.OPEN -> sessionsController().openSession(sid)
                SessionOperation.CLOSE -> sessionsController().closeSession(sid)
                SessionOperation.ADD -> {
                    val subscription = Gson.fromJson(request.body, Subscription::class.java)
                    sessionsController().setSessionFor(subscription.topic, session)
                    topicsController().addListenerOn(subscription.body, subscription.topic)
                }
                SessionOperation.REMOVE -> {
                    val subscription = Gson.fromJson(request.body, Subscription::class.java)
                    topicsController().removeListener(subscription.topic)
                }
                SessionOperation.SUBSCRIBE -> {
                    val subscription = Gson.fromJson(request.body, Subscription::class.java)
                    topicsController().removeListener(subscription.topic)
                    topicsController().addListenerOn(subscription.body, subscription.topic)
                }
                SessionOperation.UNSUBSCRIBE -> {
                    val subscription = Gson.fromJson(request.body, Subscription::class.java)
                    topicsController().removeListenerOn(subscription.body, subscription.topic)
                }
                SessionOperation.NOTIFY -> {
                } //Do Nothing
                SessionOperation.UPDATE -> {
                } //Do Nothing
            }
        }
    }
}

fun main(args: Array<String>) {

    NotifierTopicsController.init(LifeParameters.values().toSet())
    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)
}