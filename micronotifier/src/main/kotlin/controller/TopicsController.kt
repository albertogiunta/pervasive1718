package controller

import LifeParameters
import model.Member
import java.util.concurrent.atomic.AtomicBoolean

interface TopicsController<T, L> {

    fun add(topic: T, listener: L)

    fun add(topics: Iterable<T>, listener: L)

    operator fun get(topic: T): Set<L>?

    fun of(listener: L): Set<T>?

    fun removeListener(listener: L)

    fun removeListenerOn(topics: Iterable<T>, listener: L)

    fun clearListeners()

    fun activeTopics(): Set<T>

}

class NotifierTopicsController private constructor(private var topics: Set<LifeParameters>) : TopicsController<LifeParameters, Member> {

    val topicsMap = mutableMapOf<LifeParameters, MutableSet<Member>>()

    init {
        topics.forEach {
            topicsMap[it] = mutableSetOf()
        }
    }

    @Synchronized
    override fun add(topic: LifeParameters, listener: Member) {
        if (topics.contains(topic)) {
            topicsMap[topic]?.add(listener)
        }
    }

    @Synchronized
    override fun add(topics: Iterable<LifeParameters>, listener: Member) {

        val goodies = topics.filter { this.topics.contains(it) }

        if (goodies.isNotEmpty()) {
            goodies.forEach { topicsMap[it]?.add(listener) }
        }
    }

    @Synchronized
    override operator fun get(topic: LifeParameters): Set<Member>? = topicsMap[topic]

    @Synchronized
    override fun of(listener: Member): Set<LifeParameters> =
            topicsMap.filter { e -> e.value.contains(listener) }.keys

    @Synchronized
    override fun removeListener(listener: Member) {
        topicsMap.filter { it.value.contains(listener) }.forEach{
            topicsMap[it.key]?.remove(listener)
        }
    }

    @Synchronized
    override fun removeListenerOn(topics: Iterable<LifeParameters>, listener: Member) {
        topics.forEach {
            topicsMap[it]?.remove(listener)
        }
    }

    @Synchronized
    override fun clearListeners() {
        topicsMap.replaceAll { _, _ -> mutableSetOf()}
    }

    override fun activeTopics(): Set<LifeParameters> = topics

    companion object {

        private lateinit var instance: NotifierTopicsController
        private val isInitialized : AtomicBoolean = AtomicBoolean(false)

        fun init(topics: Set<LifeParameters>): NotifierTopicsController {
            if (!isInitialized.getAndSet(true)){
                instance = NotifierTopicsController(topics)
            }
            return instance
        }

        @Throws(Exception::class)
        fun singleton(): NotifierTopicsController {
            if (!isInitialized.get()) {
                throw Exception("SINGLETON not Initialized")
            } else return instance
        }

        fun singleton(topics: Set<LifeParameters>): NotifierTopicsController = init(topics)

    }
}
