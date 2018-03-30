package model

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TopicsContainerTest {

    val topicsContainer : TopicsContainer<LifeParameters, Member> = NotifierTopicsContainer(LifeParameters.values().toSet())

    val m1 = Member("Mario Rossi")
    val m2 = Member("Padre Pio")

    @Before
    fun setUp() {}

    @Test
    fun addAndGet() {
        topicsContainer.add(LifeParameters.TEMPERATURE, m1)
        assertTrue(topicsContainer[LifeParameters.TEMPERATURE]!!.contains(m1) &&
                !topicsContainer[LifeParameters.TEMPERATURE]!!.contains(m2))
    }

    @Test
    fun addMultipleAndGet() {
        topicsContainer.add(LifeParameters.values().asList(), m1)
        assertTrue(topicsContainer.of(m1).containsAll(LifeParameters.values().asList()) &&
                topicsContainer.of(m2).isEmpty())
    }

    @Test
    fun removeListener() {
        topicsContainer.add(LifeParameters.TEMPERATURE, m1)
        topicsContainer.removeListener(m1)

        assertTrue(topicsContainer.of(m1).isEmpty())
    }

    @Test
    fun removeListenerOn() {
        topicsContainer.add(LifeParameters.TEMPERATURE, m1)
        topicsContainer.removeListenerOn(LifeParameters.values().asIterable(), m1)
        assertTrue(topicsContainer.of(m1).isEmpty())
    }

    @Test
    fun clearListeners() {
        topicsContainer.add(LifeParameters.values().asIterable(), m1)
        topicsContainer.add(LifeParameters.values().asIterable(), m2)
        topicsContainer.clearListeners()

        assertTrue(topicsContainer.of(m1).isEmpty() && topicsContainer.of(m2).isEmpty())
    }

    @Test
    fun activeTopics() {

        assertArrayEquals(topicsContainer.activeTopics().toTypedArray(), LifeParameters.values())
    }
}