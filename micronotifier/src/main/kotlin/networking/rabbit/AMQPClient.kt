package networking.rabbit

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber
import io.reactivex.subjects.Subject

/**
 *  This RabbitMQ class wrapper work as relay point in order to move (@publishOn)
 *  the flux of data from AMQP to ReactiveX
 *
 *  @author XanderC
 *
 */
class AMQPClient(val broker: BrokerConnector, val topics: Map<LifeParameters, String>) {

    private val amqpSubscriber = RabbitMQSubscriber(broker)
    //val publishSubjects = ConcurrentHashMap<LifeParameters, Subject<String>>()

    init {
    }

    /**
     * By calling this method the client will start to publish the data it receives from the topics
     * that have been passed on the constructor to the publish subjects it has received.
     *
     */
    fun publishOn(publishSubjects: Map<LifeParameters, Subject<String>>) {
        topics.forEach { (lp, channel) ->
            amqpSubscriber.subscribe(channel, amqpSubscriber.createStringConsumer {
                publishSubjects[lp]?.onNext(it)
            })
        }
    }
}