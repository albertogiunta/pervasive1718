import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Matteo Gabellini on 08/02/2018.
 */
class EtCO2MonitorsFactoryTest {

    @Test
    fun createStaticEtCO2Monitor() {
        val mon = EtCO2MonitorsFactory.createStaticEtCO2Monitor()
        assertTrue(mon.currentValue() == EtCO2MonitorsFactory.DEFAULT_INIT_VALUE)
        assertEquals(mon.measuredParameter, LifeParameters.END_TIDAL_CARBON_DIOXIDE)
    }

    @Test
    fun createSimulatedEtCO2Monitor() {
        val mon = EtCO2MonitorsFactory.createSimulatedEtCO2Monitor()
        assertEquals(mon.measuredParameter, LifeParameters.END_TIDAL_CARBON_DIOXIDE)
    }
}