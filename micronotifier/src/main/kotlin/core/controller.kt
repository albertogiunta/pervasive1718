package core

import logic.Member
import spark.Session
import java.util.concurrent.ConcurrentHashMap
import LifeParameters

interface TopicController<T, L> {

    fun addListenerTo(topic: T, listener: L)

    fun getListenersOn(topic: T): Set<L>?

    fun getTopicsOf(listener: L): Set<T>?

    fun removeListener(listener: L)

    fun addTopic(topic: T)

    fun activeTopics(): Set<T>

    fun addTopics(topics: Set<T>)

    fun removeTopic(topic: T)

}

interface SessionController<L, S> {

    fun setSessionFor(listener: L, session: S)

    fun getSessionOf(listener: L): Session?

    fun removeSession(session: S)

    fun removeListener(listener: L)
}

class NotifierSessionController(private val members : Set<Member> = emptySet()) : SessionController<Member, Session> {

    val sessionsMap = ConcurrentHashMap<Member, Session>()

    override fun setSessionFor(listener: Member, session: Session) {
        sessionsMap[listener] = session
    }

    override fun getSessionOf(listener: Member): Session? = sessionsMap[listener]

    override fun removeSession(session: Session) {
        sessionsMap.keySet(session).forEach { sessionsMap.remove(it) }
    }

    override fun removeListener(listener: Member) {
        sessionsMap.remove(listener)
    }
}

class NotifierTopicController(private var topics: Set<LifeParameters>) : TopicController<LifeParameters, Member> {

    val topicsMap = ConcurrentHashMap<LifeParameters, MutableSet<Member>>()
    val listenersMap = ConcurrentHashMap<Member, MutableSet<LifeParameters>>()

    override fun addListenerTo(topic: LifeParameters, listener: Member) {
        if (topics.contains(topic)) {
            topicsMap[topic]?.add(listener)
            if (listenersMap.containsKey(listener)) {
                listenersMap[listener]?.add(topic)
            } else {
                listenersMap[listener] = mutableSetOf(topic)
            }
        }
    }

    override fun getListenersOn(topic: LifeParameters): Set<Member>? = topicsMap[topic]

    override fun getTopicsOf(listener: Member): Set<LifeParameters>? = listenersMap[listener]

    override fun removeListener(listener: Member) {
        listenersMap.remove(listener)
        topicsMap.filter { it.value.contains(listener) }.forEach{
            topicsMap[it.key]?.remove(listener)
        }
    }

    override fun addTopic(topic: LifeParameters) {
        this.topics += topic
    }

    override fun addTopics(topics: Set<LifeParameters>) {
        this.topics += topics
    }

    override fun activeTopics(): Set<LifeParameters> = topics

    override fun removeTopic(topic: LifeParameters) {

        this.topics -= topic
        topicsMap.remove(topic)
        listenersMap.keys().toList().forEach{
            listenersMap[it]?.remove(topic)
        }
    }

    init {
        with(topics) {
            topics.forEach {
                topicsMap[it] = mutableSetOf()
            }
        }
    }
}
