import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class ObservableMonitorTest {

    lateinit var mon: BasicMonitor<Double>
    val initialValue = 10.0
    val name = "Prova"
    val measuredParamenter = LifeParameters.HEART_RATE

    lateinit var simMon: SimulatedMonitor<Double>
    val minBound = 0.0
    val maxBound = 100.0
    val simulationRefreshRate = 1000L

    lateinit var obsMon: ObservableMonitor<Double>
    val observationRefreshTime = simulationRefreshRate

    @Before
    fun setUp() {
        mon = BasicMonitor(initialValue, name, measuredParamenter)
        simMon = SimulatedMonitor(mon, GenerationStrategies.DoubleLinearGeneration(minBound, maxBound), simulationRefreshRate)
        obsMon = ObservableMonitor(simMon)
    }

    @Test
    fun `The name of the observable monitor must be equal to the name of the decorated monitor`() {
        assertEquals(simMon.name, obsMon.name)
    }

    @Test
    fun `The measured parameter of the observable monitor must be equal to the measured parameter of the decorated monitor`() {
        assertEquals(obsMon.measuredParameter, simMon.measuredParameter)
    }

    @Test
    fun `The current value of the observable monitor must be equal to the measured parameter of the decorated monitor`() {
        assertTrue(obsMon.currentValue() == simMon.currentValue())
    }

    @Test
    fun `The observable object that represents the stream of the simulated data`() {
        val log = ArrayList<Double>()
        val dataFlow = obsMon.createObservable(observationRefreshTime)
        dataFlow.subscribe({ log.add(it) })
        Thread.sleep(simulationRefreshRate * 4)
        assertTrue(log.size >= 4)
    }

    @Test
    fun `The observable monitor must stop to produce data on the observable flowable after calling stopObservation`() {
        val log = ArrayList<Double>()
        val dataFlow = obsMon.createObservable(observationRefreshTime)
        dataFlow.subscribe({ log.add(it) })
        Thread.sleep(simulationRefreshRate * 4)
        obsMon.stopObservation()
        assertTrue(log.size >= 4)
        Thread.sleep(simulationRefreshRate * 2)
        assertTrue(log.size <= 7)
    }
}