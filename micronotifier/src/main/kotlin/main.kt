import amqp.AMQPClient
import core.NotifierControllerImpl
import logic.Member
import utils.Logger

fun main(args: Array<String>) {

    val controller = NotifierControllerImpl(LifeParameters.values().toSet())
    BrokerConnector.init()
    val client = AMQPClient(BrokerConnector.INSTANCE, controller)

    controller.addListenerTo(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    controller.addListenerTo(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))

    LifeParameters.values().forEach { lp ->
        client.publishSubjects[lp]?.subscribe { message ->
            controller.lifeParametersMap[lp]?.forEach { listener ->
                // Do stuff with the WebSocket
                // controller.sessionMap[listener]?.
                Logger.info("$listener ### $message")
            }
        }
    }
}
