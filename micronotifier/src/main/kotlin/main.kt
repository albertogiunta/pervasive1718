import com.google.gson.GsonBuilder
import controller.NotifierSessionsController
import controller.NotifierTopicsController
import logic.Member
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import utils.Logger

fun main(args: Array<String>) {

    val topicController = NotifierTopicsController.singleton(LifeParameters.values().toSet())
    val sessionController = NotifierSessionsController.singleton()

    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)

    BrokerConnector.init()
    val amqp = AMQPClient(BrokerConnector.INSTANCE, topicController.activeTopics())
    val gson = GsonBuilder().create()

    topicController.addListenerOn(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    topicController.addListenerOn(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))
    topicController.addListenerOn(LifeParameters.OXYGEN_SATURATION, Member(777, "Padre Pio"))

    with(amqp) {
        publishSubjects.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, Pair::class.java).run {
                    LifeParameters.valueOf(this.first.toString()) to this.second.toString().toDouble()
                }
            }.filter {
                // Check if out of boundaries and notify of the WS
                        false
                    }.doOnNext {
                        Logger.info(it.toString())
                    }.subscribe { message ->
                        // Do Stuff, if necessary but Subscription is MANDATORY.
                        topicController.topicsMap[topic]?.forEach { member ->
                            sessionController.sessionsMap[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                }
            }
        }
    }

    with(amqp) {
        publishSubjects.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, Pair::class.java).run {
                    LifeParameters.valueOf(this.first.toString()) to this.second.toString().toDouble()
                }
            }.doOnNext {
                        Logger.info(it.toString())
                    }.subscribe { message ->
                        // Do stuff with the WebSockets, dispatch only some of the merged values
                        // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
                        topicController.topicsMap[topic]?.forEach { member ->
                            Logger.info("$member ===> ${message.toJson()}")
                            sessionController.sessionsMap[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                        }
                    }
        }
    }
}

