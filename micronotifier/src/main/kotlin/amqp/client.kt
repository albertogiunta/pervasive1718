package amqp

import logic.Member
import spark.Session
import LifeParameters
import core.NotifierController
import RabbitMQSubscriber
import BrokerConnector
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

/**
 *
 *
 */
class AMQPClient(val broker: BrokerConnector,
                 val controller: NotifierController<LifeParameters, Member, Session>) {

    val amqpClient = RabbitMQSubscriber(broker)

    val handlersMap = HashMap<LifeParameters, Consumer>()

    init {
        with(controller) {
            controller.topics().forEach {
                amqpClient.subscribe(it, object : DefaultConsumer(broker.channel) {

                    @Throws(java.io.IOException::class)
                    override fun handleDelivery(consumerTag: String,
                                                envelope: Envelope,
                                                properties: AMQP.BasicProperties,
                                                body: ByteArray) {
                        val message = String(body, Charsets.UTF_8)
                        println(message)
                    }
                })
            }
        }
    }

}
