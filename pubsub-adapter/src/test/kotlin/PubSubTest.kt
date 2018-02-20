import BrokerConnector.Companion.REMOTE_HOST
import model.LifeParameters
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test

/**
 * Created by Matteo Gabellini on 25/01/2018.
 */


class PubSubTest {
    companion object {
        //Remember to start the RabbitMQ broker on the specified host
        // otherwise the system throw a ConnectionException
        private val connector: BrokerConnector

        init {
            BrokerConnector.init(LifeParameters.values().map { it.acronym }.toList(), REMOTE_HOST)
            connector = BrokerConnector.INSTANCE
        }

        @AfterClass
        @JvmStatic
        fun closeConnection() {
            BrokerConnector.INSTANCE.close()
        }
    }

    @Test
    fun singlePublish() {
        val subReceivedMessages = ArrayList<String>()

        val sub = Thread({
            val sub = RabbitMQSubscriber(connector)
            sub.subscribe(LifeParameters.HEART_RATE.acronym, sub.createStringConsumer { X -> subReceivedMessages.add(X) })
        })
        sub.start()

        Thread.sleep(2000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            pub.publish("Test 1", LifeParameters.HEART_RATE.acronym)
        })
        pub.start()

        Thread.sleep(5000)

        Assert.assertTrue(subReceivedMessages.size == 1)
    }

    @Test
    fun normalPublishingOnATopic() {
        val sub1ReceivedMessages = ArrayList<String>()
        val sub2ReceivedMessages = ArrayList<String>()

        val sub1Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            LifeParameters.values().forEach { X ->
                sub.subscribe(X.acronym, sub.createStringConsumer { sub1ReceivedMessages.add(it) })
            }
        }
        val sub2Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            LifeParameters.values().forEach { X ->
                sub.subscribe(X.acronym, sub.createStringConsumer { sub2ReceivedMessages.add(it) })
            }
        }

        val sub1 = Thread(sub1Code)
        val sub2 = Thread(sub2Code)
        sub1.start()
        sub2.start()

        Thread.sleep(2000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            for (i in 0 until 10) {
                pub.publish(i.toString(), LifeParameters.HEART_RATE.acronym)
            }
        })
        pub.start()

        Thread.sleep(5000)

        println(sub1ReceivedMessages.size)
        println(sub2ReceivedMessages.size)

        println("First")
        sub1ReceivedMessages.forEach({ X -> print(X + " ") })
        println("\n Second")
        sub2ReceivedMessages.forEach({ X -> print(X + " ") })
        Assert.assertTrue(sub1ReceivedMessages.equals(sub2ReceivedMessages))
    }


    @Test
    fun normalPublishingOnAllTopic() {
        val sub1ReceivedMessages = HashSet<String>()
        val sub2ReceivedMessages = HashSet<String>()

        val sub1Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            LifeParameters.values().forEach { X ->
                sub.subscribe(X.acronym, sub.createStringConsumer { sub1ReceivedMessages.add(it) })
            }
        }
        val sub2Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            LifeParameters.values().forEach { X ->
                sub.subscribe(X.acronym, sub.createStringConsumer { sub2ReceivedMessages.add(it) })
            }
        }

        val sub1 = Thread(sub1Code)
        val sub2 = Thread(sub2Code)
        sub1.start()
        sub2.start()

        Thread.sleep(2000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            for (i in 0 until 10) {
                LifeParameters.values().forEach { X -> pub.publish(i.toString(), X.acronym) }
            }
        })
        pub.start()

        Thread.sleep(5000)

        println(sub1ReceivedMessages.size)
        println(sub2ReceivedMessages.size)

        println("First")
        sub1ReceivedMessages.forEach({ X -> print(X + " ") })
        println("\n Second")
        sub2ReceivedMessages.forEach({ X -> print(X + " ") })
        Assert.assertTrue(sub1ReceivedMessages.equals(sub2ReceivedMessages))
    }

    @Test
    fun RabbitMQSubscriberUnsubscribing() {
        val subReceivedMessages = ArrayList<String>()

        val sub = Thread({
            val sub = RabbitMQSubscriber(connector)
            sub.subscribe(LifeParameters.HEART_RATE.acronym, sub.createStringConsumer { X -> subReceivedMessages.add(X) })
            Thread.sleep(3000)
            sub.unsubscribe(LifeParameters.HEART_RATE.acronym)
        })
        sub.start()

        Thread.sleep(2000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            for (i in 0 until 5) {
                pub.publish(i.toString(), LifeParameters.HEART_RATE.acronym)
                Thread.sleep(1000)
            }
        })
        pub.start()

        Thread.sleep(7000)

        print(subReceivedMessages.size)
        Assert.assertTrue(subReceivedMessages.size < 4)
    }


    @Test
    fun LateSubscribing() {
        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            for (i in 0 until 10) {
                pub.publish(i.toString(), LifeParameters.HEART_RATE.acronym)
            }
        })
        pub.start()

        Thread.sleep(2000)
        val subReceivedMessages = ArrayList<String>()

        val sub = Thread({
            val sub = RabbitMQSubscriber(connector)
            sub.subscribe(LifeParameters.HEART_RATE.acronym, sub.createStringConsumer { X -> subReceivedMessages.add(X) })
        })
        sub.start()

        Thread.sleep(2000)

        Assert.assertTrue(subReceivedMessages.isEmpty())
    }

    @Test
    fun NotOverlappingSubscibing() {
        val sub1ReceivedMessages = ArrayList<String>()
        val sub2ReceivedMessages = ArrayList<String>()

        val sub1Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            val consumer = sub.createStringConsumer { X -> sub1ReceivedMessages.add(X) }
            sub.subscribe(LifeParameters.HEART_RATE.acronym, consumer)
            sub.subscribe(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.acronym, consumer)
            sub.subscribe(LifeParameters.END_TIDAL_CARBON_DIOXIDE.acronym, consumer)
        }

        val sub2Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            val consumer = sub.createStringConsumer { X -> sub2ReceivedMessages.add(X) }
            sub.subscribe(LifeParameters.OXYGEN_SATURATION.acronym, consumer)
            sub.subscribe(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.acronym, consumer)
            sub.subscribe(LifeParameters.TEMPERATURE.acronym, consumer)
        }

        val sub1 = Thread(sub1Code)
        val sub2 = Thread(sub2Code)
        sub1.start()
        sub2.start()

        Thread.sleep(3000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            for (i in 0 until 10) {
                LifeParameters.values().forEach { X -> pub.publish(X.acronym + i.toString(), X.acronym) }
            }
        })
        pub.start()

        Thread.sleep(5000)

        println(sub1ReceivedMessages.size)
        Assert.assertTrue(sub1ReceivedMessages.size == 30)
        Assert.assertTrue(sub2ReceivedMessages.size == 30)

        sub1ReceivedMessages.forEach({ X -> println(X) })
        sub2ReceivedMessages.forEach({ X -> println(X) })
        Assert.assertFalse(sub1ReceivedMessages.equals(sub2ReceivedMessages))
    }

    @Test
    fun OverlappingSubscibing() {
        val sub1ReceivedMessages = ArrayList<String>()
        val sub2ReceivedMessages = ArrayList<String>()
        val overlappedElement = ArrayList<String>()

        val sub1Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            val consumer = sub.createStringConsumer { X -> sub1ReceivedMessages.add(X) }
            sub.subscribe(LifeParameters.HEART_RATE.acronym, consumer)
            sub.subscribe(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.acronym, consumer)
            sub.subscribe(LifeParameters.END_TIDAL_CARBON_DIOXIDE.acronym, consumer)
        }

        val sub2Code = Runnable {
            val sub = RabbitMQSubscriber(connector)
            val consumer = sub.createStringConsumer { X -> sub2ReceivedMessages.add(X) }
            sub.subscribe(LifeParameters.HEART_RATE.acronym, consumer)
            sub.subscribe(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.acronym, consumer)
            sub.subscribe(LifeParameters.TEMPERATURE.acronym, consumer)
        }

        val sub1 = Thread(sub1Code)
        val sub2 = Thread(sub2Code)
        sub1.start()
        sub2.start()

        Thread.sleep(3000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            for (i in 0 until 10) {
                LifeParameters.values().forEach { X -> pub.publish(X.acronym + i.toString(), X.acronym) }
                overlappedElement.add(LifeParameters.HEART_RATE.acronym + i.toString())
            }
        })
        pub.start()

        Thread.sleep(5000)

        println(sub1ReceivedMessages.size)
        Assert.assertTrue(sub1ReceivedMessages.size == 30)
        Assert.assertTrue(sub2ReceivedMessages.size == 30)


        sub1ReceivedMessages.forEach({ X -> println(X) })
        sub2ReceivedMessages.forEach({ X -> println(X) })
        Assert.assertTrue(sub1ReceivedMessages.containsAll(overlappedElement) &&
                sub2ReceivedMessages.containsAll(overlappedElement))
    }


    @Test
    fun subscribedChannel() {
        val sub = RabbitMQSubscriber(connector)
        LifeParameters.values().forEach { X ->
            sub.subscribe(X.acronym, sub.createStringConsumer { println(it) })
        }

        println(sub.subscribedTopics())
        println(LifeParameters.values().map { x -> x.acronym }.toSet())
        Assert.assertTrue(sub.subscribedTopics() == LifeParameters.values().map { x -> x.acronym }.toSet())
    }
}