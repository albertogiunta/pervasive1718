object EtCO2MonitorsFactory {
    val DEFAULT_INIT_VALUE = 40.0
    val DEFAULT_MIN_BOUND = 0.0
    val DEFAULT_MAX_BOUND = 15.0
    val DEFAULT_REFRESH_RATE = 1000L

    /**
     * A factory method for a static end tidal carbon dioxide Monitor
     * */
    fun createStaticEtCO2Monitor() = BasicMonitor(
            DEFAULT_INIT_VALUE,
            LifeParameters.END_TIDAL_CARBON_DIOXIDE.longName,
            LifeParameters.END_TIDAL_CARBON_DIOXIDE
    )

    /**
     * A factory method for a simulated end tidal carbon dioxide monitor
     * */
    fun createSimulatedEtCO2Monitor() = SimulatedMonitor(
            createStaticEtCO2Monitor(),
            GenerationStrategies.DoubleSinusoidGeneration(DEFAULT_MIN_BOUND, DEFAULT_MAX_BOUND),
            DEFAULT_REFRESH_RATE)
}