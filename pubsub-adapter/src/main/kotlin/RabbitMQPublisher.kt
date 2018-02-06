/**
 *
 * A Wrapper of a Publisher for RabbitMQ Broker
 * Created by Matteo Gabellini on 25/01/2018.
 */
class RabbitMQPublisher(val connector: BrokerConnector) : Publisher<String, LifeParameters> {

    override fun publish(message: String, topic: LifeParameters) {
        connector.channel.basicPublish(topic.acronym, "", null, message.toByteArray(charset("UTF-8")))
        println(" [x] Sent '$message' on '${topic.acronym}' ")

    }
}

fun main(argv: Array<String>) {
    //BrokerConnector.init("127.0.0.1")
    BrokerConnector.init()
    val pub = RabbitMQPublisher(BrokerConnector.INSTANCE)
    //LifeParameters.values().forEach { X -> pub.publish("Stampo su "+ X.longName, X) }
    while (true) {//for (i in 0 until 10) {
        //pub.publish(i.toString(), LifeParameters.HEART_RATE)
        pub.publish("Prova", LifeParameters.HEART_RATE)
        Thread.sleep(2000)
    }

    //BrokerConnector.INSTANCE.close()
}