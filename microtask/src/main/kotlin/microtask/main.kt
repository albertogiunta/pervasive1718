package microtask

import BrokerConnector
import LifeParameters
import RabbitMQPublisher
import RabbitMQSubscriber

fun main(args: Array<String>) {
    val pub = RabbitMQPublisher(BrokerConnector.init().let { BrokerConnector.INSTANCE })
    pub.publish("Test 1", LifeParameters.HEART_RATE)

    val sub = RabbitMQSubscriber(BrokerConnector.init().let { BrokerConnector.INSTANCE })
    sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer { X -> println(X) })
}