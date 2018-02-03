/**
 * Singleton used to manage the connection with the RabbitMQ Broker
 *
 * Created by Matteo Gabellini on 25/01/2018.
 */

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.util.concurrent.atomic.AtomicBoolean

class BrokerConnector private constructor(host: String) {

    private val factory: ConnectionFactory = ConnectionFactory()
    private val connection: Connection
    val channel: Channel

    init {
        with(factory) {
            this.host = host
            username = "pervasive"
            password = "zeronegativo"
        }
        connection = factory.newConnection()
        channel = connection.createChannel()
        LifeParameters.values().forEach { X ->
            channel.exchangeDeclare(X.acronym, "fanout")
        }
    }

    fun getNewQueue(): String = channel.queueDeclare().queue

    fun close() {
        channel.close()
        connection.close()
    }

    companion object {
        const val LOCAL_HOST = "localhost"
        const val REMOTE_HOST = "http://2.234.121.101:4369/"

        lateinit var INSTANCE: BrokerConnector
        val isInitialized = AtomicBoolean()
        fun init(host: String = REMOTE_HOST) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = BrokerConnector(host)

                with(INSTANCE.factory) {
                    username = "pervasive"
                    password = "zeronegativo"
                }
            }
        }
    }
}
