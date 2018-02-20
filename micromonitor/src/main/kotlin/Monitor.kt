import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import model.LifeParameters
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The interface for a basic monitor that measure life parameters
 *
 * @param T the type used to rappresent the life parameter values (ex: Integer, Double, Long, ecc...)
 * */
interface Monitor<T> {

    /**
     * Get the current value measured by the monitor
     * */
    fun currentValue(): T

    /**
     * Get the monitor's name
     * */
    val name: String
    val measuredParameter: LifeParameters

}

/**
 * A basic implementation of a monitor
 * */
class BasicMonitor<T>(private val initialValue: T, override val name: String, override val measuredParameter: LifeParameters) : Monitor<T> {
    override fun currentValue() = initialValue
}

/**
 * A decoration for a generic monitor in order to adds a simulated behaviour
 * */
class SimulatedMonitor<T>(decoratedMonitor: Monitor<T>, generationLogic: GenerationLogic<T>, refreshRate: Long) : Monitor<T> {

    override val name: String
    override val measuredParameter: LifeParameters

    @Volatile
    private var value: T
    private val executor = Executors.newScheduledThreadPool(1)
    private val changeLogic: Runnable


    init {
        name = decoratedMonitor.name
        value = decoratedMonitor.currentValue()
        measuredParameter = decoratedMonitor.measuredParameter

        changeLogic = Runnable { this.setCurrentValue(generationLogic.nextValue()) }
        this.executor.scheduleAtFixedRate(changeLogic, 0L, refreshRate, TimeUnit.MILLISECONDS)
    }

    //thread safe read/write-access to the simulated current value
    @Synchronized
    private fun getCurrentValue() = value

    @Synchronized
    private fun setCurrentValue(v: T) {
        value = v
    }

    override fun currentValue() = this.getCurrentValue()

    fun stopGeneration() {
        this.executor.shutdown()
    }
}

/**
 * A decoration for a generic monitor in order to adds the logic of observable data flow creation
 * according to the RxKotlin (ReactiveX API)
 * */
class ObservableMonitor<T>(private val observedMonitor: Monitor<T>) : Observable<T>, Monitor<T> {

    override val name: String = observedMonitor.name
    override val measuredParameter: LifeParameters = observedMonitor.measuredParameter

    @Volatile
    private var continueObservation = true

    override fun currentValue(): T = observedMonitor.currentValue()

    override fun createObservable(refreshPeriod: Long): Flowable<T> = Flowable.create<T>({ emitter ->
        continueObservation = true
        Thread({
            var curVal: T
            while (continueObservation) {
                try {
                    curVal = observedMonitor.currentValue()
                    emitter.onNext(curVal)
                    Thread.sleep(refreshPeriod)
                } catch (ex: Exception) {
                    println(ex.stackTrace)
                }
            }
        }).start()
    }, BackpressureStrategy.BUFFER)

    override fun stopObservation() {
        continueObservation = false
    }
}


