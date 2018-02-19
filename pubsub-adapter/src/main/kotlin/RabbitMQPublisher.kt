/**
 *
 * A Wrapper of a Publisher for RabbitMQ Broker
 * Created by Matteo Gabellini on 25/01/2018.
 */
class RabbitMQPublisher(val connector: BrokerConnector) : Publisher<String, String> {

    override fun publish(message: String, topic: String) {
        connector.channel.basicPublish(topic, "", null, message.toByteArray(charset("UTF-8")))
//        println(" [x] Sent '$message' on '${topic}' ")

    }
}

fun main(argv: Array<String>) {
    BrokerConnector.init(LifeParameters.values().map { it.acronym }.toList())
    val pub = RabbitMQPublisher(BrokerConnector.INSTANCE)
    while (true) {//for (i in 0 until 10) {
        pub.publish("Prova", LifeParameters.HEART_RATE.acronym)
        Thread.sleep(2000)
    }

}