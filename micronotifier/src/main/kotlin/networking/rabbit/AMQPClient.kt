package networking.rabbit

import BrokerConnector
import RabbitMQSubscriber
import io.reactivex.subjects.Subject
import model.LifeParameters

/**
 *  This RabbitMQ class wrapper work as relay point in order to move (@publishOn)
 *  the flux of data from AMQP to ReactiveX
 *
 *  @author XanderC
 *
 */
class AMQPClient(private val topics: Map<LifeParameters, String>) {

    private val subscriber : RabbitMQSubscriber

    init {
        BrokerConnector.init(topics.values.toList())

        subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
    }

    /**
     * By calling this method the client will start to publish the data it receives from the topics
     * that have been passed on the constructor to the publish subjects it has received.
     *
     */
    fun publishOn(publishSubjects: Map<LifeParameters, Subject<String>>) {
        topics.forEach { (lp, channel) ->
            subscriber.subscribe(channel, subscriber.createStringConsumer {
                publishSubjects[lp]?.onNext(it)
            })
        }
    }
}