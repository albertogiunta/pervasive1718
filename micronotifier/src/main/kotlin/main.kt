import amqp.AMQPClient
import core.NotifierTopicController
import logic.Member
import utils.Logger
import com.google.gson.GsonBuilder
import core.NotifierSessionController


fun main(args: Array<String>) {

    val topicController = NotifierTopicController(LifeParameters.values().toSet())
    val sessionController= NotifierSessionController()

    BrokerConnector.init()
    val client = AMQPClient(BrokerConnector.INSTANCE, topicController)

    val gson = GsonBuilder().create()

    topicController.addListenerTo(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    topicController.addListenerTo(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))
    topicController.addListenerTo(LifeParameters.OXYGEN_SATURATION, Member(777, "Padre Pio"))

    topicController.activeTopics().forEach { lp ->
        client.publishSubjects[lp]
            ?.filter {
                // Check if out of boundaries and notify of the WS
                true
            }?.doOnNext {
//                Logger.info(it)
            }?.subscribe {
                // Do Stuff, if necessary
                // Subscription is MANDATORY.
                topicController.topicsMap[lp]?.forEach{
                    sessionController.sessionsMap[it] // Notify the WS, dunno how.
                }
            }
    }

    client.publishSubjects.forEach { _, stream ->
        stream.map {
            mapOf(gson.fromJson(it, Pair::class.java))
        }.doOnNext{
//            Logger.info(it.toJson())
        }.subscribe{ message ->
            // Do stuff with the WebSockets, dispatch only some of the merged values
            // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
            topicController.listenersMap.forEach { m, lps -> // Member -> Set<LifeParameters>
                // Do Stuff
                message.filter { e ->
                    lps.map{it.toString()}.toSet().contains(e.key)
                }.forEach {
                    Logger.info("$m ===> ${it.toJson()}")
                            sessionController.sessionsMap[m] // Notify the WS, dunno how.
                }
            }
        }
    }
}
