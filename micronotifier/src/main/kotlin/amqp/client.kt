package amqp

import logic.Member
import spark.Session
import LifeParameters
import core.NotifierController
import RabbitMQSubscriber
import BrokerConnector
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

/**
 *
 *
 */
class AMQPClient(val broker: BrokerConnector,
                 val controller: NotifierController<LifeParameters, Member, Session>) {

    val amqpSubscriber = RabbitMQSubscriber(broker)
    val publishSubjects = ConcurrentHashMap<LifeParameters, Subject<String>>()

    init {
        with(controller) {
            controller.topics().forEach { lp ->
                publishSubjects[lp] = PublishSubject.create<String>()
                amqpSubscriber.subscribe(lp, amqpSubscriber.createStringConsumer {
                    publishSubjects[lp]?.onNext(it)
                })
            }
        }
    }
}