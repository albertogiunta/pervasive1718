package amqp

import logic.Member
import LifeParameters
import core.TopicController
import RabbitMQSubscriber
import BrokerConnector
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

/**
 *
 *
 */
class AMQPClient(val broker: BrokerConnector, val controller: TopicController<LifeParameters, Member>) {

    private val amqpSubscriber = RabbitMQSubscriber(broker)
    val publishSubjects = ConcurrentHashMap<LifeParameters, Subject<String>>()

    init {
        with(controller) {
            controller.activeTopics().forEach { lp ->
                publishSubjects[lp] = PublishSubject.create<String>()
                amqpSubscriber.subscribe(lp, amqpSubscriber.createStringConsumer {
                    publishSubjects[lp]?.onNext(it)
                })
            }
        }
    }
}