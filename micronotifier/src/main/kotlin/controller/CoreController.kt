package controller

import controller.logic.NotificationHandler
import controller.logic.RelayHandler
import controller.logic.SubscriptionHandler
import io.reactivex.subjects.PublishSubject
import model.LifeParameters
import model.Member
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session
import patterns.Observer
import utils.Logger

class CoreController private constructor(topicSet: Set<LifeParameters>) : Observer {

    var topics: TopicsManager<LifeParameters, Member> = NotifierTopicsManager(topicSet)
    var sessions: SessionsManager<Member, Session> = NotifierSessionsManager()
    var sources: SourcesManager<String, Any> = NotifierSourcesManager()

    @Volatile
    var useLogging = false

    init { }

    fun withLogging() : CoreController {
        this.useLogging = true
        return this
    }

    fun loadHandlers() : CoreController {
        SubscriptionHandler.runOn(this)
        RelayHandler.runOn(this)
        NotificationHandler.runOn(this)

        return this
    }

    fun withoutLogging() : CoreController {
        this.useLogging = false
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun notify(obj: Any) {
        if (obj is Pair<*, *> && obj.first is String) {
            val identifier= obj.first as String

            when(obj.first) {
                AMQPClient::class.java.toString() -> {
                    val (lp, source) = obj.second as Pair<LifeParameters, PublishSubject<String>>
                    Logger.info("Adding Observable Source $source for $lp")
                    sources.addNewObservableSource(lp.toString(), source.publish().autoConnect())
                }

                RelayService::class.java.toString() -> {
                    val source = obj.second as PublishSubject<Pair<Session, String>>
                    Logger.info("Adding Observable Source $source for $identifier")
                    sources.addNewObservableSource(identifier, source.publish().autoConnect())
                }
            }
        }
    }

    companion object {
        private var instance: CoreController = CoreController(LifeParameters.values().toSet())

        fun singleton(): CoreController = instance
    }
}