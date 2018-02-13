package controller

import LifeParameters
import com.google.gson.GsonBuilder
import logic.Member
import org.eclipse.jetty.websocket.api.Session

class CoreController private constructor(topicSet: Set<LifeParameters>) {

    var topics: TopicsController<LifeParameters, Member> = NotifierTopicsController.init(topicSet)
    var sessions: SessionsController<Member, Session> = NotifierSessionsController.singleton()
    var subjects: SubjectsController<String, Any> = NotifierSubjectsController.singleton()

    private val gson = GsonBuilder().create()

    init {
        val channel = subjects.createNewSubjectFor<Pair<Session, String>>(CoreController::class.java.name)

        val publishSubjects = topics.activeTopics().map {
            it to subjects.createNewSubjectFor<String>(it.toString())
        }.toMap()
    }

    companion object {
        private var instance: CoreController = CoreController(LifeParameters.values().toSet())

        fun singleton(): CoreController = instance
    }
}

