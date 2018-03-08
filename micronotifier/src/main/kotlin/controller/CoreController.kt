package controller

import controller.logic.NotificationHandler
import controller.logic.RelayHandler
import controller.logic.SubscriptionHandler
import model.LifeParameters
import model.Member
import networking.ws.RelayService
import org.eclipse.jetty.websocket.api.Session

class CoreController private constructor(topicSet: Set<LifeParameters>) {

    var topics: TopicsManager<LifeParameters, Member> = NotifierTopicsManager(topicSet)
    var sessions: SessionsManager<Member, Session> = NotifierSessionsManager()
    var subjects: SubjectsManager<String, Any> = NotifierSubjectsManager()

    @Volatile
    var useLogging = false

    init { }

    fun loadSubjects() : CoreController {
        subjects.createNewSubjectFor<Pair<Session, String>>(RelayService::class.java.name)

        topics.activeTopics().map {
            it to subjects.createNewSubjectFor<String>(it.toString())
        }
        return this
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

    companion object {
        private var instance: CoreController = CoreController(LifeParameters.values().toSet())

        fun singleton(): CoreController = instance
    }
}