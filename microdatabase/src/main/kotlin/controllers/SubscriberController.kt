package controllers

import BrokerConnector
import LifeParameters
import RabbitMQSubscriber
import controllers.api.LogApi
import utils.acronymWithSession

object SubscriberController {

    private lateinit var subscriber: RabbitMQSubscriber


    fun startListeningMonitorsForInstanceId(instanceId: Int) {
        if (InstanceIdController.attachInstanceId(instanceId)) {
            BrokerConnector.init(LifeParameters.values().map { it.acronymWithSession(instanceId) }.toList())
            subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
            LifeParameters.values().forEach { param ->
                subscriber.subscribe(param.acronymWithSession(InstanceIdController.getCurrentInstanceID()), subscriber.createStringConsumer { value ->
                    //                    println("Saving param $param value $value to session ${getCurrentInstanceID()}")
                    if (InstanceIdController.isAttached()) LogApi.addLogEntry(param, value.toDouble())
                })
            }
        }
    }

    fun stopListeningMonitorsForSession() {
        try {
            LifeParameters.values().forEach { subscriber.unsubscribe(it.acronymWithSession(InstanceIdController.getCurrentInstanceID())) }
            InstanceIdController.detachInstance()
        } catch (e: Exception) {
            println("Got exception in amq unsubscribe $e")
        }
    }
}