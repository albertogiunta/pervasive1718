package controllers

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber
import controllers.SessionController.getCurrentSession
import controllers.api.LogApi
import utils.acronymWithSession

object SubscriberController {

    private lateinit var subscriber: RabbitMQSubscriber


    fun startListeningMonitorsForSession(sessionId: Int) {
        BrokerConnector.init(LifeParameters.values().map { it.acronymWithSession(sessionId) }.toList())
        subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
        if (SessionController.attachSession(sessionId)) {
            LifeParameters.values().forEach { param ->
                subscriber.subscribe(param.acronym, subscriber.createStringConsumer { value ->
                    println("Saving param $param value $value to session ${getCurrentSession()}")
                    LogApi.addLogEntry(param, value.toDouble())
                })
            }
        }
    }

    fun stopListeningMonitorsForSession() {
        try {
            SessionController.detachSession()
            LifeParameters.values().forEach { subscriber.unsubscribe(it.acronym) }
        } catch (e: Exception) {
            println("Got exception in amq unsubscribe $e")
        }
    }
}