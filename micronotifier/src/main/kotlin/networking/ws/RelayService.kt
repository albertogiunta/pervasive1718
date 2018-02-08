package networking.ws

import LifeParameters
import WSParams
import WSServer
import WSServerInitializer
import com.google.gson.GsonBuilder
import controller.NotifierTopicController
import controller.TopicController
import logic.Member
import model.Payload
import model.Subscription
import model.SubscriptionOperation
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.Logger

@WebSocket
class RelayService : WSServer<Payload<LifeParameters, Any>>() {

    val topicController: () -> TopicController<LifeParameters, Member> = { NotifierTopicController.singleton() }
    val Gson = GsonBuilder().create()

    init {
        Logger.info(topicController().activeTopics().toString())
    }

    override fun message(session: Session, message: String) {
        super.message(session, message)

        val request = Gson.fromJson(message, Subscription::class.java)

        with(request) {
            when (topic) {
                SubscriptionOperation.SUBSCRIBE -> {
                    topicController().addListenerTo(this.body.second, this.body.first)
                }
                SubscriptionOperation.UNSUBSCRIBE -> {
                    topicController().removeListenerOn(this.body.second, this.body.first)
                }
                SubscriptionOperation.REMOVE -> {
                    topicController().removeListener(this.body.first)
                }
            }
        }
    }
}

fun main(args: Array<String>) {

    NotifierTopicController.init(LifeParameters.values().toSet())

    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)
}