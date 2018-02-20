import model.LifeParameters
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class BasicMonitorTest {

    lateinit var mon: BasicMonitor<Double>
    val initialValue = 10.0
    val name = "Prova"
    val measuredParamenter = LifeParameters.HEART_RATE

    @Before
    fun initialization() {
        mon = BasicMonitor(initialValue, name, measuredParamenter)
    }

    @Test
    fun currentValue() {
        Assert.assertTrue(mon.currentValue() == initialValue)
    }

    @Test
    fun getName() {
        Assert.assertTrue(mon.name == name)
    }

    @Test
    fun getMeasuredParameter() {
        Assert.assertTrue(mon.measuredParameter == measuredParamenter)
    }
}