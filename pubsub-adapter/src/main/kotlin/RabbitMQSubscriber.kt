import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

/**
 *
 * Wrapper of a Subscriber for RabbitMq Broker
 *
 * Created by Matteo Gabellini on 25/01/2018.
 */
class RabbitMQSubscriber(val connector: BrokerConnector) : Subscriber<String, Consumer> {

    private var subscribedChannel: HashMap<String, String> = HashMap()

    override fun subscribe(topic: String, consumingLogic: Consumer) {
        if (!subscribedChannel.containsKey(topic)) {
            val queueName: String = connector.getNewQueue()

            connector.channel.queueBind(queueName, topic, "")
            subscribedChannel.put(
                    topic,
                    connector.channel.basicConsume(queueName, true, consumingLogic
                    ))
        }
    }

    override fun unsubscribe(topic: String) {
        connector.channel.basicCancel(subscribedChannel.get(topic))
        subscribedChannel.remove(topic)
    }


    override fun subscribedTopics(): Set<String> {
        val set = HashSet<String>()
        subscribedChannel.keys.forEach { set.add(it) }
        return set
    }

    fun createStringConsumer(messageHandler: (String) -> Unit): Consumer {
        return object : DefaultConsumer(connector.channel) {
            @Throws(java.io.IOException::class)
            override fun handleDelivery(consumerTag: String,
                                        envelope: Envelope,
                                        properties: AMQP.BasicProperties,
                                        body: ByteArray) {
                val message = String(body, Charsets.UTF_8)
                messageHandler(message)
            }
        }
    }
}

fun main(argv: Array<String>) {
    BrokerConnector.init()
    val sub = RabbitMQSubscriber(BrokerConnector.INSTANCE)

    val consumer = sub.createStringConsumer { X ->
        println(X)
    }
    LifeParameters.values().forEach { X -> sub.subscribe(X.acronym, consumer) }
}