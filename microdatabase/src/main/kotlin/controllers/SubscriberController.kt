package controllers

import BrokerConnector
import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import RabbitMQSubscriber
import com.github.kittinunf.fuel.httpPost
import config.Services
import model.LifeParameters
import model.Log
import model.Session
import utils.acronymWithSession
import utils.toJson
import java.sql.Timestamp
import java.util.*

object SubscriberController {

    private lateinit var subscriber: RabbitMQSubscriber

    private val addLog = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/${Params.Log.API_NAME}"

    fun startListeningMonitorsForInstanceId(session: Session) {
        if (SessionController.attachInstanceId(session)) {
            BrokerConnector.init(LifeParameters.values().map { it.acronymWithSession(SessionController.getCurrentInstanceId()) }.toList())
            subscriber = RabbitMQSubscriber(BrokerConnector.INSTANCE)
            LifeParameters.values().forEach { param ->
                subscriber.subscribe(param.acronymWithSession(SessionController.getCurrentInstanceId()), subscriber.createStringConsumer { value ->
                    val log = Log(-1, param.longName, Timestamp(Date().time), param.id, value.toDouble())
                    if (SessionController.isAttached()) addLog.httpPost().body(log.toJson()).responseString()
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