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

    /**
     * The name of the observable monitor must be equal to the name of the decorated monitor
     * */
    @Test
    fun getName() {
        assertEquals(simMon.name, obsMon.name)
    }

    /**
     * The measured parameter of the observable monitor must be equal to the measured parameter of the decorated monitor
     * */
    @Test
    fun getMeasuredParameter() {
        assertEquals(obsMon.measuredParameter, simMon.measuredParameter)
    }

    /**
     * The current value of the observable monitor must be equal to the measured parameter of the decorated monitor
     * */
    @Test
    fun currentValue() {
        assertTrue(obsMon.currentValue() == simMon.currentValue())
    }

    /**
     * The create observable must create an RxKotlin Flowable object that
     * rappresents the stream of the simulated data
     * */
    @Test
    fun createObservable() {
        var log = ArrayList<Double>()
        var dataFlow = obsMon.createObservable(observationRefreshTime)
        dataFlow.subscribe({ log.add(it) })
        Thread.sleep(simulationRefreshRate * 4)
        assertTrue(log.size >= 4)
    }

    /**
     *  The observable monitor must stop to produce data on the observable flowable
     *  after calling stopObservation
     */
    @Test
    fun stopObservation() {
        var log = ArrayList<Double>()
        var dataFlow = obsMon.createObservable(observationRefreshTime)
        dataFlow.subscribe({ log.add(it) })
        Thread.sleep(simulationRefreshRate * 4)
        obsMon.stopObservation()
        assertTrue(log.size >= 4)
        Thread.sleep(simulationRefreshRate * 2)
        assertTrue(log.size <= 7)
    }
}