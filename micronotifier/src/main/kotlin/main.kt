import com.google.gson.GsonBuilder
import controller.NotifierSessionController
import controller.NotifierTopicController
import logic.Member
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import utils.Logger

fun main(args: Array<String>) {

    val topicController = NotifierTopicController.singleton(LifeParameters.values().toSet())
    val sessionController = NotifierSessionController.singleton()

    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)

    BrokerConnector.init()
    val amqp = AMQPClient(BrokerConnector.INSTANCE, topicController.activeTopics())
    val gson = GsonBuilder().create()

    topicController.addListenerTo(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    topicController.addListenerTo(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))
    topicController.addListenerTo(LifeParameters.OXYGEN_SATURATION, Member(777, "Padre Pio"))

    topicController.activeTopics().forEach { lp ->
        amqp.publishSubjects[lp]
                ?.map {
                    gson.fromJson(it, Pair::class.java).run {
                        LifeParameters.valueOf(this.first.toString()) to this.second.toString().toDouble()
                    }
                }?.filter {
                // Check if out of boundaries and notify of the WS
                    false
            }?.doOnNext {
//                Logger.info(it)
                }?.subscribe { message ->
                    // Do Stuff, if necessary but Subscription is MANDATORY.
                topicController.topicsMap[lp]?.forEach{
                    sessionController.sessionsMap[it]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                }
            }
    }

    amqp.publishSubjects.forEach { _, stream ->
        stream.map {
            gson.fromJson(it, Pair::class.java).run {
                LifeParameters.valueOf(this.first.toString()) to this.second.toString().toDouble()
            }
        }.doOnNext{
                    Logger.info(it.toString())
        }.subscribe{ message ->
            // Do stuff with the WebSockets, dispatch only some of the merged values
            // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
                    topicController.listenersMap.forEach { member, itsTopics ->
                        // Member -> Set<LifeParameters>
                        when (itsTopics.contains(message.first)) {
                            true -> {
                                Logger.info("$member ===> ${message.toJson()}")
                                sessionController.sessionsMap[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                            }
                }
            }
        }
    }
}
