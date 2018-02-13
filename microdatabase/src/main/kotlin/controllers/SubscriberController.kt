package controllers

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber
import controllers.SessionController.getCurrentSession
import controllers.api.LogApi

object SubscriberController {

    private val subscriber: RabbitMQSubscriber

    init {
        BrokerConnector.init()
        subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
    }

    fun startListeningMonitorsForSession(sessionId: Int) {
        if (SessionController.attachSession(sessionId)) {
            LifeParameters.values().forEach { param ->
                subscriber.subscribe(param, subscriber.createStringConsumer { value ->
                    println("Saving param $param value $value to session ${getCurrentSession()}")
                    LogApi.addLogEntry(param, value.toDouble())
                })
            }
        }
    }

    fun stopListeningMonitorsForSession(sessionId: Int) {
        SessionController.detachSession()
        LifeParameters.values().forEach { subscriber.unsubscribe(it) }
    }
}