package controller

import LifeParameters
import logic.Member
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.atomic.AtomicBoolean

class CoreController private constructor(topicSet: Set<LifeParameters>) {

    var topics: TopicsController<LifeParameters, Member> = NotifierTopicsController.init(topicSet)
    var sessions: SessionsController<Member, Session> = NotifierSessionsController.singleton()
    var subjects: SubjectsController<String, String> = SubjectsController.singleton()

    companion object {
        private lateinit var instance: CoreController
        private val isInitialized: AtomicBoolean = AtomicBoolean(false)

        fun init(topics: Set<LifeParameters>): CoreController {
            if (!isInitialized.getAndSet(true)) {
                instance = CoreController(topics)
            }
            return instance
        }

        @Throws(Exception::class)
        fun singleton(): CoreController {
            if (!isInitialized.get()) {
                throw Exception("SINGLETON not Initialized")
            } else return instance
        }

        fun singleton(topics: Set<LifeParameters>): CoreController = init(topics)
    }
}

