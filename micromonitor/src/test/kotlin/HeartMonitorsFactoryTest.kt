import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Matteo Gabellini on 08/02/2018.
 */
class HeartMonitorsFactoryTest {

    @Test
    fun createStaticSystolicBloodPressureMonitor() {
        val mon = HeartMonitorsFactory.createStaticSystolicBloodPressureMonitor()
        assertTrue(mon.currentValue() == HeartMonitorsFactory.DEFAULT_SYSTOLIC_BLOOD_PRESSURE_INIT_VAL)
        assertEquals(mon.measuredParameter, LifeParameters.SYSTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun createStaticDiastolicBloodPressureMonitor() {
        val mon = HeartMonitorsFactory.createStaticDiastolicBloodPressureMonitor()
        assertTrue(mon.currentValue() == HeartMonitorsFactory.DEFAULT_DIASTOLIC_BLOOD_PRESSURE_INIT_VAL)
        assertEquals(mon.measuredParameter, LifeParameters.DIASTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun createStaticHeartRateMonitor() {
        val mon = HeartMonitorsFactory.createStaticHeartRateMonitor()
        assertTrue(mon.currentValue() == HeartMonitorsFactory.DEFAULT_HEART_RATE_INIT_VAL)
        assertEquals(mon.measuredParameter, LifeParameters.HEART_RATE)
    }

    @Test
    fun createSimulatedSystolicPressureMonitor() {
        val mon = HeartMonitorsFactory.createSimulatedSystolicPressureMonitor()
        assertEquals(mon.measuredParameter, LifeParameters.SYSTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun createSimulatedDiastolicPressureMonitor() {
        val mon = HeartMonitorsFactory.createSimulatedDiastolicPressureMonitor()
        assertEquals(mon.measuredParameter, LifeParameters.DIASTOLIC_BLOOD_PRESSURE)
    }

    @Test
    fun createSimulatedHeartRateMonitor() {
        val mon = HeartMonitorsFactory.createSimulatedHeartRateMonitor()
        assertEquals(mon.measuredParameter, LifeParameters.HEART_RATE)
    }
}