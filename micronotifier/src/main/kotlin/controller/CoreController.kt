package controller

import controller.logic.NotificationHandler
import controller.logic.RelayHandler
import controller.logic.SubscriptionHandler
import io.reactivex.subjects.PublishSubject
import model.LifeParameters
import model.Member
import model.PayloadWrapper
import model.WSOperations
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session
import utils.Logger
import utils.toJson

class CoreController private constructor(topicSet: Set<LifeParameters>) : patterns.Observer {

    var topics: TopicsManager<LifeParameters, Member> = NotifierTopicsManager(topicSet)
    var sessions: SessionsManager<Member, Session> = NotifierSessionsManager()
    var sources: SourcesManager<String, Any> = NotifierSourcesManager()

    private var amqpSubjects : Map<LifeParameters, PublishSubject<String>>
    private var wsSubject : PublishSubject<Pair<Session, String>>
    @Volatile
    var useLogging = false

    init {

        amqpSubjects = topics.activeTopics().map {
            it to PublishSubject.create<String>()
        }.toMap()

        amqpSubjects.forEach { lp, source ->
            Logger.info("Adding Observable Source $source for $lp")
            sources.addNewObservableSource(lp.toString(), source.publish().autoConnect())
        }

        wsSubject = PublishSubject.create<Pair<Session, String>>()
        Logger.info("Adding Observable Source $wsSubject. for ${RelayService::class.java.name}")
        sources.addNewObservableSource(RelayService::class.java.name, wsSubject.publish().autoConnect())

    }

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
    override fun update(obj: Any) {
        when (obj) {
            is Pair<*, *> -> {
                when(obj.first) {
                    is Session -> {
                        wsSubject.onNext(obj as Pair<Session, String>)
                    }

                    is LifeParameters -> {
                        val (lp, message) = obj as Pair<LifeParameters, String>
                        amqpSubjects[lp]?.onNext(message)
                    }
                    else -> {}
                }
            }
            is Session -> {
                if (sessions.has(obj)) {
                    val message = PayloadWrapper(-1, WSOperations.CLOSE, sessions.getOn(obj)!!.toJson()).toJson()
                    wsSubject.onNext(obj to message)
                }
            }
            else -> {}
        }
    }

    companion object {
        private var instance: CoreController = CoreController(LifeParameters.values().toSet())

        fun singleton(): CoreController = instance
    }
}