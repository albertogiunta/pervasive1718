package controllers

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber
import controllers.api.LogApi
import model.Session
import utils.acronymWithSession

object SubscriberController {

    private lateinit var subscriber: RabbitMQSubscriber


    fun startListeningMonitorsForInstanceId(session: Session) {
        if (SessionController.attachInstanceId(session)) {
            BrokerConnector.init(LifeParameters.values().map { it.acronymWithSession(SessionController.getCurrentInstanceId()) }.toList())
            subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
            LifeParameters.values().forEach { param ->
                subscriber.subscribe(param.acronymWithSession(SessionController.getCurrentInstanceId()), subscriber.createStringConsumer { value ->
                    //                    println("Saving param $param value $value to session ${getCurrentInstanceId()}")
                    if (SessionController.isAttached()) LogApi.addLogEntry(param, value.toDouble())
                })
            }
        }
    }

    fun stopListeningMonitorsForSession() {
        try {
            LifeParameters.values().forEach { subscriber.unsubscribe(it.acronymWithSession(SessionController.getCurrentInstanceId())) }
            SessionController.detachInstance()
        } catch (e: Exception) {
            println("Got exception in amq unsubscribe $e")
        }
    }
}