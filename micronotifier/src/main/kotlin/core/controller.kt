package core

import logic.Member
import spark.Session
import java.util.concurrent.ConcurrentHashMap
import LifeParameters
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.HashMap

interface NotifierController<T, L, S> {

    fun setSessionFor(listener: L, session: S)

    fun getSessionOf(listener: L): Session?

    fun removeSession(session: S)

    fun addListenerTo(topic: T, listener: L)

    fun getListenersOn(topic: T): Set<L>?

    fun removeListener(listener: L)

    fun addTopic(topic: T)

    fun topics(): Set<T>

    fun addTopics(topics: Set<T>)

    fun removeTopic(topic: T)

}

class NotifierControllerImpl(private var topics: Set<LifeParameters>) : NotifierController<LifeParameters, Member, Session> {

    val lifeParametersMap = ConcurrentHashMap<LifeParameters, Set<Member>>()
    val sessionMap = ConcurrentHashMap<Member, Session>()

    override fun setSessionFor(listener: Member, session: Session) {
        sessionMap[listener] = session
    }

    override fun getSessionOf(listener: Member): Session? = sessionMap[listener]

    override fun removeSession(session: Session) {
        sessionMap.keySet(session).forEach { sessionMap.remove(it) }
    }

    override fun addListenerTo(topic: LifeParameters, listener: Member) {
        topics += topic
        lifeParametersMap.merge(topic, setOf(listener), { t, u -> t + u })
    }

    override fun getListenersOn(topic: LifeParameters): Set<Member>? = lifeParametersMap[topic]

    override fun removeListener(listener: Member) {
        sessionMap.remove(listener)
    }

    override fun addTopic(topic: LifeParameters) {
        topics += topic
    }

    override fun addTopics(topics: Set<LifeParameters>) {
        this.topics += topics
    }

    override fun topics(): Set<LifeParameters> = topics

    override fun removeTopic(topic: LifeParameters) {
        topics -= topic
        lifeParametersMap.remove(topic)
    }

    init {
        with(topics) {
            topics.forEach {
                lifeParametersMap[it] = emptySet()
            }
        }
    }

}
