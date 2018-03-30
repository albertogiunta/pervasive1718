package controller

import controller.logic.NotificationHandler
import controller.logic.RelayHandler
import controller.logic.SubscriptionHandler
import io.reactivex.subjects.PublishSubject
import model.*
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session
import utils.Logger
import utils.toJson
import java.util.concurrent.ConcurrentHashMap

class CoreController private constructor(topicSet: Set<LifeParameters>) : patterns.Observer {

    var topics: TopicsContainer<LifeParameters, Member> = NotifierTopicsContainer(topicSet)
    var sessions: SessionsContainer<Member, Session> = NotifierSessionsContainer()
    var sources: SourcesContainer<String, Any> = NotifierSourcesContainer()

    private val amqpSubjects = ConcurrentHashMap<LifeParameters, PublishSubject<String>>()
    private val wsSubjects = ConcurrentHashMap<String, PublishSubject<Pair<Session, String>>>()

    @Volatile
    var useLogging = false

    init {

        topics.activeTopics().forEach { lp ->
            amqpSubjects[lp] = PublishSubject.create<String>()
            Logger.info("Adding Observable Source $amqpSubjects[lp] for $lp")
            sources.addNewObservableSource(lp.toString(), amqpSubjects[lp]!!.publish().autoConnect())
        }

        wsSubjects[RelayService::class.java.name] = PublishSubject.create<Pair<Session, String>>()
        Logger.info("Adding Observable Source ${wsSubjects[RelayService::class.java.name]} for ${RelayService::class.java.name}")
        sources.addNewObservableSource(RelayService::class.java.name, wsSubjects[RelayService::class.java.name]!!.publish().autoConnect())
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
                when(obj) {
                    obj.first is Session && obj.second is String -> {
                        wsSubjects[RelayService::class.java.name]?.onNext(obj as Pair<Session, String>)
                    }

                    obj.first is LifeParameters && obj.second is String -> {
                        val (lp, message) = obj as Pair<LifeParameters, String>
                        amqpSubjects[lp]?.onNext(message)
                    }
                    else -> {}
                }
            }
            is Session -> {
                if (sessions.has(obj)) {
                    val message = PayloadWrapper(-1, WSOperations.CLOSE, sessions.getOn(obj)!!.toJson())
                    wsSubjects[RelayService::class.java.name]?.onNext(obj to message.toJson())
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