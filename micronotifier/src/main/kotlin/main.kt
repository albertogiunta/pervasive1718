import com.google.gson.GsonBuilder
import controller.CoreController
import logic.Member
import model.PayloadWrapper
import model.SessionOperation
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()
    val core = CoreController.singleton()

    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)

    BrokerConnector.init()
    val amqp = AMQPClient(BrokerConnector.INSTANCE, core.topics.activeTopics())

    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

    // This should be placed into the WS Class
    // Check OUT OF BOUND Heath Parameters
    with(publishSubjects) {
        this.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, kotlin.Pair::class.java)
            }.map { (lp, value) ->
                        LifeParameters.valueOf(lp.toString()) to value.toString().toDouble()
            }.filter {
                        // Check if out of boundaries and notify of the WS
                        false
                    }.doOnNext {
                        utils.Logger.info(it.toString())
                    }.subscribe { (lp, value) ->
                        val message = PayloadWrapper(-1L,
                                model.SessionOperation.NOTIFY,
                                model.Notification(-1L, setOf(lp), "...").toJson()
                        )
                        // Do Stuff, if necessary but Subscription is MANDATORY.
                        core.topics[topic]?.forEach { member ->
                            core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                }
            }
        }
    }

    // This should be pushed into the WS Class
    // Simple relays received Health Values to Listeners
    with(publishSubjects) {
        this.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, kotlin.Pair::class.java)
            }.map { (lp, value) ->
                        LifeParameters.valueOf(lp.toString()) to value.toString().toDouble()
            }.doOnNext {
                        utils.Logger.info(it.toString())
                    }.subscribe { (lp, value) ->
                        val message = PayloadWrapper(-1L,
                                model.SessionOperation.UPDATE,
                                model.Update(-1L, lp, value).toJson()
                        )
                        // Do stuff with the WebSockets, dispatch only some of the merged values
                        // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
                        core.topics[topic]?.forEach { member ->
                            utils.Logger.info("$member ===> ${message.toJson()}")
                            core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                        }
                    }
        }
    }

    val channel = core.subjects.getSubjectsOf<Pair<Session, String>>(core.toString())!!

    channel.map { (session, json) ->
        session to gson.fromJson(json, PayloadWrapper::class.java)
    }.subscribe { (session, wrapper) ->
                // Probably this should be moved into the controller => Pattern patterns.Observer
                // If a Pattern patterns.Observer is used then both the controllers should be wrapped by a core controller
                // Where the whole observation behavior is handled
                when (wrapper.subject) {
                    SessionOperation.CLOSE -> core.sessions.close()
                    SessionOperation.ADD -> {
                        val subscription = gson.fromJson(wrapper.body, model.Subscription::class.java)
                        core.sessions[subscription.subject] = session
                        core.topics.add(subscription.body, subscription.subject)
                    }
                    SessionOperation.REMOVE -> {
                        val subscription = gson.fromJson(wrapper.body, model.Subscription::class.java)
                        core.topics.removeListener(subscription.subject)
                    }
                    SessionOperation.SUBSCRIBE -> {
                        val subscription = gson.fromJson(wrapper.body, model.Subscription::class.java)
                        core.topics.removeListener(subscription.subject)
                        core.topics.add(subscription.body, subscription.subject)
                    }
                    SessionOperation.UNSUBSCRIBE -> {
                        val subscription = gson.fromJson(wrapper.body, model.Subscription::class.java)
                        core.topics.removeListenerOn(subscription.body, subscription.subject)
                    }

                    else -> { // Do Nothing at all
                    }
                }
            }

    core.topics.add(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    core.topics.add(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))
    core.topics.add(LifeParameters.OXYGEN_SATURATION, Member(777, "Padre Pio"))
}

