import com.google.gson.GsonBuilder
import controller.CoreController
import logic.Member
import model.PayloadWrapper
import model.SessionOperation
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session
import utils.Logger

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()

    val core = CoreController.singleton()

    WSServerInitializer.init(RelayService::class.java, WSParams.WS_NOTIFIER_PORT, WSParams.WS_PATH_NOTIFIER)

    BrokerConnector.init()
    val amqp = AMQPClient(BrokerConnector.INSTANCE, core.topics.activeTopics())

    val coreSubject = core.subjects.getSubjectsOf<Pair<Session, String>>(CoreController::class.java.name)!!
    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

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

    // Simple relays received Health Values to Listeners
    with(publishSubjects) {
        this.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, kotlin.Pair::class.java)
            }.map { (lp, value) ->
                        LifeParameters.valueOf(lp.toString()) to value.toString().toDouble()
            }.doOnNext {
//                utils.Logger.info(it.toString())
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

