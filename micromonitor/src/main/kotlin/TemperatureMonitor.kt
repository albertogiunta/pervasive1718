import model.LifeParameters

object TemperatureMonitorsFactory {

    val DEFAULT_INIT_VALUE = 80
    val DEFAULT_MIN_BOUND = 36.0
    val DEFAULT_MAX_BOUND = 43.0
    val DEFAULT_REFRESH_RATE = 3000L

    /**
     * A factory method for a static Temperature Monitor
     * */
    fun createStaticTemperatureMonitor() = BasicMonitor(
            36.0,
            LifeParameters.TEMPERATURE.longName,
            LifeParameters.TEMPERATURE
    )

    /**
     * A factory method for a simulated temperature monitor
     * */
    fun createSimulatedTemperatureMonitor() = SimulatedMonitor(
            createStaticTemperatureMonitor(),
        GenerationStrategies.DoubleSinusoidGeneration(DEFAULT_MIN_BOUND, DEFAULT_MAX_BOUND),
        DEFAULT_REFRESH_RATE)
}