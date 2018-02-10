package controller

import LifeParameters
import logic.Member
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

interface TopicsController<T, L> {

    fun addListenerOn(topic: T, listener: L)

    fun addListenerOn(topics: Iterable<T>, listener: L)

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

class NotifierTopicsController private constructor(private var topics: Set<LifeParameters>) : TopicsController<LifeParameters, Member> {

    val topicsMap = ConcurrentHashMap<LifeParameters, MutableSet<Member>>()

    init {
        topics.forEach {
            topicsMap[it] = mutableSetOf()
        }
    }

    override fun addListenerOn(topic: LifeParameters, listener: Member) {
        if (topics.contains(topic)) {
            topicsMap[topic]?.add(listener)
        }
    }

    override fun addListenerOn(topics: Iterable<LifeParameters>, listener: Member) {

        val goodies = topics.filter { this.topics.contains(it) }

        if (goodies.isNotEmpty()) {
            goodies.forEach { topicsMap[it]?.add(listener) }
        }
    }

    override fun getListenersOn(topic: LifeParameters): Set<Member>? = topicsMap[topic]

    override fun getTopicsOf(listener: Member): Set<LifeParameters>? =
            topicsMap.filter { e -> e.value.contains(listener) }.keys

    override fun removeListener(listener: Member) {
        topicsMap.filter { it.value.contains(listener) }.forEach{
            topicsMap[it.key]?.remove(listener)
        }
    }

    override fun removeListenerOn(topics: Iterable<LifeParameters>, listener: Member) {
        topics.forEach {
            topicsMap[it]?.remove(listener)
        }
    }

    override fun clearListeners() {
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
    }

    companion object {

        private lateinit var controller: NotifierTopicsController
        private val isInitialized : AtomicBoolean = AtomicBoolean(false)

        fun init(topics: Set<LifeParameters>): NotifierTopicsController {
            if (!isInitialized.getAndSet(true)){
                controller = NotifierTopicsController(topics)
            }
            return controller
        }

        @Throws(Exception::class)
        fun singleton(): NotifierTopicsController {
            if (!isInitialized.get()) {
                throw Exception("SINGLETON not Initialized")
            } else return controller
        }

        fun singleton(topics: Set<LifeParameters>): NotifierTopicsController = init(topics)

    }
}
