object SpO2MonitorsFactory {

    val DEFAULT_INIT_VALUE = 80
    val DEFAULT_MIN_BOUND = 0
    val DEFAULT_MAX_BOUND = 100
    val DEFAULT_REFRESH_RATE = 1000L

    /**
     * A factory method for a static Oxygen saturation Monitor
     * */
    fun createStaticSpO2Monitor() = BasicMonitor(
            DEFAULT_INIT_VALUE,
            LifeParameters.OXYGEN_SATURATION.longName,
            LifeParameters.OXYGEN_SATURATION
    )

    /**
     * A factory method for a simulated oxygen saturation monitor
     * */
    fun createSimulatedSpO2Monitor() = SimulatedMonitor(
            createStaticSpO2Monitor(),
            GenerationStrategies.IntSinusoidGeneration(DEFAULT_MIN_BOUND, DEFAULT_MAX_BOUND),
            DEFAULT_REFRESH_RATE)
}