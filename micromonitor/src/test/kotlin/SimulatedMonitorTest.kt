import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SimulatedMonitorTest {

    lateinit var mon: BasicMonitor<Double>
    val initialValue = 10.0
    val name = "Prova"
    val measuredParamenter = LifeParameters.HEART_RATE

    lateinit var simMon: SimulatedMonitor<Double>
    val minBound = 0.0
    val maxBound = 100.0
    val refreshRate: Long = 1000

    @Before
    fun initialization() {
        mon = BasicMonitor(initialValue, name, measuredParamenter)
        simMon = SimulatedMonitor(mon, GenerationStrategies.DoubleLinearGeneration(minBound, maxBound), refreshRate)
    }

    /**
     * The name of the simulated monitor must be equal to the name of the decorated monitor
     * */
    @Test
    fun getName() {
        assertEquals(simMon.name, mon.name)
    }


    /**
     * The measured parameter of the simulated monitor must be equal to the measured parameter of the decorated monitor
     * */
    @Test
    fun getMeasuredParameter() {
        assertEquals(simMon.measuredParameter, mon.measuredParameter)
    }

    /**
     * The value of the simulated monitor with a linear generation strategy must be changed after the refresh period
     */
    @Test
    fun currentValue() {
        var prevVal = simMon.currentValue()
        Thread.sleep(refreshRate)
        var curVal = simMon.currentValue()
        assertTrue(prevVal != curVal)
        prevVal = curVal
        Thread.sleep(refreshRate)
        curVal = simMon.currentValue()
        assertTrue(prevVal != curVal)
    }

    /**
     * After calling stop generation the simulated monitor (with a linear strategy generation) must be stop to change its value
     * */
    @Test
    fun stopGeneration() {
        var prevVal = simMon.currentValue()
        Thread.sleep(refreshRate)
        var curVal = simMon.currentValue()
        simMon.stopGeneration()
        assertTrue(prevVal != curVal)

        Thread.sleep(refreshRate * 2)
        prevVal = curVal
        Thread.sleep(refreshRate)
        curVal = simMon.currentValue()
        println("Prev val: " + prevVal)
        println("Curval : " + curVal)
        assertTrue(prevVal == curVal)
    }
}