package networking.rabbit

import LifeParameters
import RabbitMQSubscriber
import BrokerConnector
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

/**
 *  @author XanderC
 *
 */
class AMQPClient(val broker: BrokerConnector, val topics: Set<LifeParameters>) {

    private val amqpSubscriber = RabbitMQSubscriber(broker)
    val publishSubjects = ConcurrentHashMap<LifeParameters, Subject<String>>()

    init {
        with(topics) {
            forEach { lp ->
                publishSubjects[lp] = PublishSubject.create<String>()
                amqpSubscriber.subscribe(lp, amqpSubscriber.createStringConsumer {
                    publishSubjects[lp]?.onNext(it)
                })
            }
        }
    }
}