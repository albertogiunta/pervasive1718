package controller

import logic.Member
import java.util.concurrent.ConcurrentHashMap
import LifeParameters
import java.util.concurrent.atomic.AtomicBoolean

interface TopicController<T, L> {

    fun addListenerTo(topic: T, listener: L)

    fun addListenerTo(topics: Iterable<T>, listener: L)

    fun getListenersOn(topic: T): Set<L>?

    fun getTopicsOf(listener: L): Set<T>?

    fun removeListener(listener: L)

    fun removeListenerOn(topics: Iterable<T>, listener: L)

    fun clearListeners()

    fun addTopic(topic: T)

    fun addTopics(topics: Set<T>)

    fun activeTopics(): Set<T>

    fun removeTopic(topic: T)

}

class NotifierTopicController private constructor(private var topics: Set<LifeParameters>) : TopicController<LifeParameters, Member> {

    val topicsMap = ConcurrentHashMap<LifeParameters, MutableSet<Member>>()
    val listenersMap = ConcurrentHashMap<Member, MutableSet<LifeParameters>>()

    init {
        with(topics) {
            topics.forEach {
                topicsMap[it] = mutableSetOf()
            }
        }
    }

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

    override fun addListenerTo(topics: Iterable<LifeParameters>, listener: Member) {

        val goodies = topics.filter { this.topics.contains(it) }

        if (goodies.isNotEmpty()) {
            if (listenersMap.containsKey(listener)) {
                listenersMap[listener]?.addAll(goodies)
            } else {
                listenersMap[listener] = goodies.toMutableSet()
            }
            goodies.forEach { topicsMap[it]?.add(listener) }
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

    override fun removeListenerOn(topics: Iterable<LifeParameters>, listener: Member) {
        topics.forEach {
            topicsMap[it]?.remove(listener)
        }

        listenersMap[listener]?.removeAll(topics)
    }

    override fun clearListeners() {
        listenersMap.clear()
        topicsMap.replaceAll { _, _ -> mutableSetOf()}
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

    companion object {

        private lateinit var controller: NotifierTopicController
        private val isInitialized : AtomicBoolean = AtomicBoolean(false)

        fun init(topics : Set<LifeParameters>) : NotifierTopicController {

            if (!isInitialized.getAndSet(true)){
                controller = NotifierTopicController(topics)
            }

            return controller
        }

        @Throws(Exception::class)
        fun singleton() : NotifierTopicController {
            if (!isInitialized.get()) {
                throw Exception("SINGLETON not Initialized")
            } else return controller
        }

        fun singleton(topics : Set<LifeParameters>) : NotifierTopicController = init(topics)

    }
}
