package controller

import LifeParameters
import controller.logic.NotificationHandler
import controller.logic.RelayHandler
import controller.logic.SubscriptionHandler
import model.Member
import org.eclipse.jetty.websocket.api.Session

class CoreController private constructor(topicSet: Set<LifeParameters>) {

    var topics: TopicsController<LifeParameters, Member> = NotifierTopicsController(topicSet)
    var sessions: SessionsController<Member, Session> = NotifierSessionsController()
    var subjects: SubjectsController<String, Any> = NotifierSubjectsController()

    init {
        subjects.createNewSubjectFor<Pair<Session, String>>(CoreController::class.java.name)

        topics.activeTopics().map {
            it to subjects.createNewSubjectFor<String>(it.toString())
        }

        SubscriptionHandler.runOn(this)
        RelayHandler.runOn(this)
        NotificationHandler.runOn(this)
    }

    companion object {
        private var instance: CoreController = CoreController(LifeParameters.values().toSet())

        fun singleton(): CoreController = instance
    }
}

