import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SimulatedMonitorTest {

    private lateinit var mon: BasicMonitor<Double>
    private val initialValue = 10.0
    private val name = "Prova"
    private val measuredParamenter = LifeParameters.HEART_RATE

    private lateinit var simMon: SimulatedMonitor<Double>
    private val minBound = 0.0
    private val maxBound = 100.0
    private val refreshRate: Long = 1000

    @Before
    fun setup() {
        mon = BasicMonitor(initialValue, name, measuredParamenter)
        simMon = SimulatedMonitor(mon, GenerationStrategies.DoubleLinearGeneration(minBound, maxBound), refreshRate)
    }

    @Test
    fun `The name of the simulated monitor must be equal to the name of the decorated monitor`() {
        assertEquals(simMon.name, mon.name)
    }

    @Test
    fun `The measured parameter of the simulated monitor must be equal to the measured parameter of the decorated monitor`() {
        assertEquals(simMon.measuredParameter, mon.measuredParameter)
    }

    @Test
    fun `The value of the simulated monitor with a linear generation strategy must be changed after the refresh period`() {
        var prevVal = simMon.currentValue()
        Thread.sleep(refreshRate)
        var curVal = simMon.currentValue()
        assertTrue(prevVal != curVal)
        prevVal = curVal
        Thread.sleep(refreshRate)
        curVal = simMon.currentValue()
        assertTrue(prevVal != curVal)
    }

    @Test
    fun `After calling stop generation the simulated monitor (with a linear strategy generation) must be stop to change its value`() {
        var prevVal = simMon.currentValue()
        Thread.sleep(refreshRate)
        var curVal = simMon.currentValue()
        simMon.stopGeneration()
        assertTrue(prevVal != curVal)

        Thread.sleep(refreshRate * 2)
        prevVal = simMon.currentValue()
        Thread.sleep(refreshRate)
        curVal = simMon.currentValue()
        println("Prev val: " + prevVal)
        println("Curval : " + curVal)
        assertTrue(prevVal == curVal)
    }
}