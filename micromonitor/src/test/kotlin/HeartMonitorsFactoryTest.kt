import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Matteo Gabellini on 08/02/2018.
 */
class HeartMonitorsFactoryTest {

    @Test
    fun `create Static Systolic Blood Pressure Monitor`() {
        val mon = HeartMonitorsFactory.createStaticSystolicBloodPressureMonitor()
        assertTrue(mon.currentValue() == HeartMonitorsFactory.DEFAULT_SYSTOLIC_BLOOD_PRESSURE_INIT_VAL)
        assertEquals(mon.measuredParameter, LifeParameters.SYSTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun `create Static Diastolic Blood Pressure Monitor`() {
        val mon = HeartMonitorsFactory.createStaticDiastolicBloodPressureMonitor()
        assertTrue(mon.currentValue() == HeartMonitorsFactory.DEFAULT_DIASTOLIC_BLOOD_PRESSURE_INIT_VAL)
        assertEquals(mon.measuredParameter, LifeParameters.DIASTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun `create Static Heart Rate Monitor`() {
        val mon = HeartMonitorsFactory.createStaticHeartRateMonitor()
        assertTrue(mon.currentValue() == HeartMonitorsFactory.DEFAULT_HEART_RATE_INIT_VAL)
        assertEquals(mon.measuredParameter, LifeParameters.HEART_RATE)
    }

    @Test
    fun `create Simulated Systolic Pressure Monitor`() {
        val mon = HeartMonitorsFactory.createSimulatedSystolicPressureMonitor()
        assertEquals(mon.measuredParameter, LifeParameters.SYSTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun `create Simulated Diastolic Pressure Monitor`() {
        val mon = HeartMonitorsFactory.createSimulatedDiastolicPressureMonitor()
        assertEquals(mon.measuredParameter, LifeParameters.DIASTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun `create Simulated Heart Rate Monitor`() {
        val mon = HeartMonitorsFactory.createSimulatedHeartRateMonitor()
        assertEquals(mon.measuredParameter, LifeParameters.HEART_RATE)
    }
}