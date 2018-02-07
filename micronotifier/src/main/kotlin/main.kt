import amqp.AMQPClient
import core.NotifierControllerImpl
import io.reactivex.Observable
import logic.Member
import utils.Logger

fun main(args: Array<String>) {

    val controller = NotifierControllerImpl(LifeParameters.values().toSet())
    BrokerConnector.init()
    val client = AMQPClient(BrokerConnector.INSTANCE, controller)

    controller.addListenerTo(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    controller.addListenerTo(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))

    controller.topics().forEach { lp ->
        client.publishSubjects[lp]
        ?.doOnNext {
            // Check if out of boundaries and notify of the WS
            Logger.info("Do on Next... Something with $it")

            controller.lifeParametersMap[lp]?.forEach{
                controller.sessionsMap[it] // Notify the WS, dunno how.
            }
        }
        ?.subscribe {
            // Do Stuff, if necessary
            // Subscription is MANDATORY.
        }
    }

    Observable.combineLatest(client.publishSubjects.values, {
        // Merge Values Together, possible into a json
        // prefix, separator, postfix let merge single json key -> values
        it.joinToString(prefix = "{", separator = ",", postfix = "}")
    }).subscribe{ message ->
        // Do stuff with the WebSockets, dispatch only some of the merged values
        // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
        Logger.info(message)

        controller.listenersMap.forEach { k, v -> // Member -> Set<LifeParameters>
            // Do Stuff

            controller.sessionsMap[k] // Notify the WS, dunno how.
        }
    }
}
