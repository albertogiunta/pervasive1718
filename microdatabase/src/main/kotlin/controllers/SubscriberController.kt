package controllers

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber
import controllers.SessionController.getCurrentSession
import controllers.api.LogApi
import utils.acronymWithSession

object SubscriberController {

    private val subscriber: RabbitMQSubscriber

    init {
        BrokerConnector.init(LifeParameters.values().map { it.acronymWithSession(argv) }.toList())
        subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
    }

    fun startListeningMonitorsForSession(sessionId: Int) {
        if (SessionController.attachSession(sessionId)) {
            LifeParameters.values().forEach { param ->
                subscriber.subscribe(param.acronym, subscriber.createStringConsumer { value ->
                    println("Saving param $param value $value to session ${getCurrentSession()}")
                    LogApi.addLogEntry(param, value.toDouble())
                })
            }
        }
    }

    fun stopListeningMonitorsForSession(sessionId: Int) {
        try {
            SessionController.detachSession()
            LifeParameters.values().forEach { subscriber.unsubscribe(it.acronym) }
        } catch (e: Exception) {
            println("Got exception in amq unsubscribe $e")
        }
    }
}