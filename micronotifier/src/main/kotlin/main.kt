import amqp.AMQPClient
import core.NotifierControllerImpl
import logic.Member
import utils.Logger
import com.google.gson.GsonBuilder


fun main(args: Array<String>) {

    val controller = NotifierControllerImpl(LifeParameters.values().toSet())
    BrokerConnector.init()
    val client = AMQPClient(BrokerConnector.INSTANCE, controller)

    val gson = GsonBuilder().create()

    controller.addListenerTo(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    controller.addListenerTo(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))
    controller.addListenerTo(LifeParameters.OXYGEN_SATURATION, Member(777, "Padre Pio"))

    controller.topics().forEach { lp ->
        client.publishSubjects[lp]
            ?.filter {
                // Check if out of boundaries and notify of the WS
                true
            }?.doOnNext {
//                Logger.info(it)
            }?.subscribe {
                // Do Stuff, if necessary
                // Subscription is MANDATORY.
                controller.lifeParametersMap[lp]?.forEach{
                    controller.sessionsMap[it] // Notify the WS, dunno how.
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
            controller.listenersMap.forEach { m, lps -> // Member -> Set<LifeParameters>
                // Do Stuff
                message.filter { e ->
                    lps.map{it.toString()}.toSet().contains(e.key)
                }.forEach {
                    Logger.info("$m ===> ${it.toJson()}")
                    controller.sessionsMap[m] // Notify the WS, dunno how.
                }
            }
        }
    }
}
