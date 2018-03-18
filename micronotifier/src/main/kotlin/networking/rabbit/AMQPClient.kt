package networking.rabbit

import BrokerConnector
import RabbitMQSubscriber
import model.LifeParameters
import patterns.Observer

/**
 *  This RabbitMQ class wrapper work as relay point in order to move (@publishOn)
 *  the flux of data from AMQP to ReactiveX
 *
 *  @author XanderC
 *
 */
class AMQPClient(private val topics: Map<LifeParameters, String>) : patterns.Observable {

    private val subscriber : RabbitMQSubscriber
    private val observers = mutableListOf<Observer>()
    init {
        BrokerConnector.init(topics.values.toList())

        subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {   }

    /**
     * By calling this method the client will start to publish the data it receives from the topics
     * that have been passed on the constructor to the publish subjects created.
     *
     */
    fun startPublishing() {
        topics.forEach { (lp, channel) ->
            subscriber.subscribe(channel, subscriber.createStringConsumer {value ->
                observers.parallelStream().forEach{it.notify(lp to value)}
            })
        }
    }
}