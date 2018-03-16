package controller

import model.LifeParameters
import model.Member
import java.util.concurrent.ConcurrentHashMap

interface TopicsManager<T, L> {

    fun add(topic: T, listener: L)

    fun add(topics: Iterable<T>, listener: L)

    operator fun get(topic: T): Set<L>?

    fun of(listener: L): Set<T>?

    fun removeListener(listener: L)

    fun removeListenerOn(topics: Iterable<T>, listener: L)

    fun clearListeners()

    fun activeTopics(): Set<T>

}

class NotifierTopicsManager(private var topics: Set<LifeParameters>) : TopicsManager<LifeParameters, Member> {

    private val topicsMap = ConcurrentHashMap<LifeParameters, MutableSet<Member>>()

    init {
        topics.forEach {
            topicsMap[it] = mutableSetOf()
        }
    }

    override fun add(topic: LifeParameters, listener: Member) {
        if (topics.contains(topic)) {
            topicsMap[topic]?.add(listener)
        }
    }

    override fun add(topics: Iterable<LifeParameters>, listener: Member) {

        val goodies = topics.filter { this.topics.contains(it) }

        if (goodies.isNotEmpty()) {
            goodies.forEach { topicsMap[it]?.add(listener) }
        }
    }

    override operator fun get(topic: LifeParameters): Set<Member>? = topicsMap[topic]

    override fun of(listener: Member): Set<LifeParameters> =
            topicsMap.filter { e -> e.value.contains(listener) }.keys

    override fun removeListener(listener: Member) =
        topicsMap.filter { it.value.contains(listener) }.forEach{
            topicsMap[it.key]?.remove(listener)
        }

    override fun removeListenerOn(topics: Iterable<LifeParameters>, listener: Member) =
        topics.forEach {
            topicsMap[it]?.remove(listener)
        }

    override fun clearListeners() = topicsMap.replaceAll { _, _ -> mutableSetOf() }

    override fun activeTopics(): Set<LifeParameters> = topics
}
