import model.LifeParameters
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class BasicMonitorTest {

    private lateinit var monitor: BasicMonitor<Double>
    private val initialValue = 10.0
    private val name = "Prova"
    private val measuredParameter = LifeParameters.HEART_RATE

    @Before
    fun setup() {
        monitor = BasicMonitor(initialValue, name, measuredParameter)
    }

    @Test
    fun `test monitor's initial value`() {
        Assert.assertTrue(monitor.currentValue() == initialValue)
    }

    @Test
    fun `test monitor's name`() {
        Assert.assertTrue(monitor.name == name)
    }

    @Test
    fun `test monitor's measured parameter type`() {
        Assert.assertTrue(monitor.measuredParameter == measuredParameter)
    }
}