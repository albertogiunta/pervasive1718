package microtask

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber

fun main(args: Array<String>) {
    val sub = RabbitMQSubscriber(BrokerConnector.init().let { BrokerConnector.INSTANCE })
    sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer { X -> println(X) })
}