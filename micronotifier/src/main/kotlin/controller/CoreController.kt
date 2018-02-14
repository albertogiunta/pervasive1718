package controller

import LifeParameters
import com.google.gson.GsonBuilder
import controller.logic.Notification
import controller.logic.Relay
import controller.logic.Subscription
import logic.Member
import org.eclipse.jetty.websocket.api.Session

class CoreController private constructor(topicSet: Set<LifeParameters>) {

    var topics: TopicsController<LifeParameters, Member> = NotifierTopicsController.init(topicSet)
    var sessions: SessionsController<Member, Session> = NotifierSessionsController.singleton()
    var subjects: SubjectsController<String, Any> = NotifierSubjectsController.singleton()

    private val gson = GsonBuilder().create()

    init {
        subjects.createNewSubjectFor<Pair<Session, String>>(CoreController::class.java.name)

        topics.activeTopics().map {
            it to subjects.createNewSubjectFor<String>(it.toString())
        }

        Relay.run(this)
        Notification.run(this)
        Subscription.run(this)
    }

    companion object {
        private var instance: CoreController = CoreController(LifeParameters.values().toSet())

        fun singleton(): CoreController = instance
    }
}

