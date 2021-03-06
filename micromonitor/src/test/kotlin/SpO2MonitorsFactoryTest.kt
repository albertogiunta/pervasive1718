import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpO2MonitorsFactoryTest {

    @Test
    fun `create Static SpO2 Monitor`() {
        val mon = SpO2MonitorsFactory.createStaticSpO2Monitor()
        assertTrue(mon.currentValue() == SpO2MonitorsFactory.DEFAULT_INIT_VALUE)
        assertEquals(mon.measuredParameter, LifeParameters.OXYGEN_SATURATION)
    }

    @Test
    fun `create Simulated SpO2 Monitor`() {
        val mon = SpO2MonitorsFactory.createSimulatedSpO2Monitor()
        assertEquals(mon.measuredParameter, LifeParameters.OXYGEN_SATURATION)
    }
}