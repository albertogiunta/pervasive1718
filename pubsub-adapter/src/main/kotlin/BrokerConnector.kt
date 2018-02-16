/**
 * Singleton used to manage the connection with the RabbitMQ Broker
 *
 * Created by Matteo Gabellini on 25/01/2018.
 */

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.util.concurrent.atomic.AtomicBoolean

class BrokerConnector private constructor(topics: List<String>, host: String) {

    private val factory: ConnectionFactory = ConnectionFactory()
    private val connection: Connection
    val channel: Channel

    init {
        with(factory) {
            this.host = host
            if (host != LOCAL_HOST) {
                username = "pervasive"
                password = "zeronegativo"
            }
        }
        connection = factory.newConnection()
        channel = connection.createChannel()
        topics.forEach { X ->
            channel.exchangeDeclare(X, "fanout")
        }
    }

    fun getNewQueue(): String = channel.queueDeclare().queue

    fun close() {
        channel.close()
        connection.close()
    }

    companion object {
        const val LOCAL_HOST = "127.0.0.1"
        const val REMOTE_HOST = "2.234.121.101"

        lateinit var INSTANCE: BrokerConnector
        val isInitialized = AtomicBoolean()
        fun init(topics: List<String>, host: String = REMOTE_HOST) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = BrokerConnector(topics, host)

                with(INSTANCE.factory) {
                    username = "pervasive"
                    password = "zeronegativo"
                }
            }
        }
    }
}
