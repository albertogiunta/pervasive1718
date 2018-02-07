package core

import logic.Member
import spark.Session
import java.util.concurrent.ConcurrentHashMap
import LifeParameters

interface NotifierController<T, L, S> {

    fun topics(): Set<T>

    fun setSessionFor(listener: L, session: S)

    fun removeListener(listener: L)

    fun removeSession(session: S)

    fun addListenerTo(topic: T, listener: L)

    fun addTopic(topic: T)

    fun addTopics(topics: Set<T>)

    fun removeTopic(topic: T)

}

class NotifierControllerImpl(private var topics: Set<LifeParameters>) : NotifierController<LifeParameters, Member, Session> {

    val lifeParametersMap = ConcurrentHashMap<LifeParameters, Set<Member>>()
    val sessionMap = ConcurrentHashMap<Member, Session>()

    override fun topics(): Set<LifeParameters> {
        return topics
    }

    override fun setSessionFor(listener: Member, session: Session) {
        sessionMap[listener] = session
    }

    override fun removeListener(listener: Member) {
        sessionMap.remove(listener)
    }

    override fun removeSession(session: Session) {
        sessionMap.keySet(session).forEach { sessionMap.remove(it) }
    }

    override fun addListenerTo(topic: LifeParameters, listener: Member) {
        topics += topic
        lifeParametersMap.merge(topic, setOf(listener), { t, u -> t + u })
    }

    override fun addTopic(topic: LifeParameters) {
        topics += topic
    }

    override fun addTopics(topics: Set<LifeParameters>) {
        this.topics += topics
    }

    override fun removeTopic(topic: LifeParameters) {
        topics -= topic
        lifeParametersMap.remove(topic)
    }

    init {

    }

}
