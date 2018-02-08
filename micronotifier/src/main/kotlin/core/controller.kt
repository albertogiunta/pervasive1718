package core

import logic.Member
import spark.Session
import java.util.concurrent.ConcurrentHashMap
import LifeParameters

interface NotifierController<T, L, S> {

    fun setSessionFor(listener: L, session: S)

    fun getSessionOf(listener: L): Session?

    fun removeSession(session: S)

    fun addListenerTo(topic: T, listener: L)

    fun getListenersOn(topic: T): Set<L>?

    fun getTopicsOf(listener: L): Set<T>?

    fun removeListener(listener: L)

    fun addTopic(topic: T)

    fun topics(): Set<T>

    fun addTopics(topics: Set<T>)

    fun removeTopic(topic: T)

}

class NotifierControllerImpl(private var topics: Set<LifeParameters>) : NotifierController<LifeParameters, Member, Session> {

    val lifeParametersMap = ConcurrentHashMap<LifeParameters, MutableSet<Member>>()
    val listenersMap = ConcurrentHashMap<Member, MutableSet<LifeParameters>>()
    val sessionsMap = ConcurrentHashMap<Member, Session>()

    override fun setSessionFor(listener: Member, session: Session) {
        sessionsMap[listener] = session
    }

    override fun getSessionOf(listener: Member): Session? = sessionsMap[listener]

    override fun removeSession(session: Session) {
        sessionsMap.keySet(session).forEach { sessionsMap.remove(it) }
    }

    override fun addListenerTo(topic: LifeParameters, listener: Member) {
        if (topics.contains(topic)) {
            lifeParametersMap[topic]?.add(listener)
            listenersMap[listener]?.add(topic)
        }
    }

    override fun getListenersOn(topic: LifeParameters): Set<Member>? = lifeParametersMap[topic]
    override fun getTopicsOf(listener: Member): Set<LifeParameters>? = listenersMap[listener]

    override fun removeListener(listener: Member) {
        sessionsMap.remove(listener)
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
        listenersMap.keys().toList().forEach{
            listenersMap[it]?.remove(topic)
        }
    }

    init {
        with(topics) {
            topics.forEach {
                lifeParametersMap[it] = mutableSetOf()
            }
        }
    }

}
