package microtask

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber

fun main(args: Array<String>) {
    BrokerConnector.init("localhost")
    val connector = BrokerConnector.INSTANCE
    val sub = RabbitMQSubscriber(connector)
    sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer { X -> println(X) })
}