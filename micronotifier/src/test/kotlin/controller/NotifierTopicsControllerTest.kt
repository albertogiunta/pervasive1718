package controller

import model.LifeParameters
import model.Member
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotifierTopicsControllerTest {

    val controller = NotifierTopicsController(LifeParameters.values().toSet())

    val m1 = Member("Mario Rossi")
    val m2 = Member("Padre Pio")

    @Before
    fun onLoad() {

    }

    @Test
    fun addAndGet() {
        controller.add(LifeParameters.TEMPERATURE, m1)
        assertTrue(controller[LifeParameters.TEMPERATURE]!!.contains(m1) &&
                !controller[LifeParameters.TEMPERATURE]!!.contains(m2))
    }

    @Test
    fun addMultipleAndGet() {
        controller.add(LifeParameters.values().asList(), m1)
        assertTrue(controller.of(m1).containsAll(LifeParameters.values().asList()) &&
                controller.of(m2).isEmpty())
    }

    @Test
    fun removeListener() {
        controller.add(LifeParameters.TEMPERATURE, m1)
        controller.removeListener(m1)

        assertTrue(controller.of(m1).isEmpty())
    }

    @Test
    fun removeListenerOn() {
        controller.add(LifeParameters.TEMPERATURE, m1)
        controller.removeListenerOn(LifeParameters.values().asIterable(), m1)
        assertTrue(controller.of(m1).isEmpty())
    }

    @Test
    fun clearListeners() {
        controller.add(LifeParameters.values().asIterable(), m1)
        controller.add(LifeParameters.values().asIterable(), m2)
        controller.clearListeners()

        assertTrue(controller.of(m1).isEmpty() && controller.of(m2).isEmpty())
    }

    @Test
    fun activeTopics() {

        assertArrayEquals(controller.activeTopics().toTypedArray(), LifeParameters.values())
    }
}