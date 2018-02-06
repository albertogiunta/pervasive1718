object EtCO2MonitorsFactory {
    /**
     * A factory method for a static end tidal carbon dioxide Monitor
     * */
    fun createStaticEtCO2Monitor() = BasicMonitor(
            40.0,
            LifeParameters.END_TIDAL_CARBON_DIOXIDE.longName,
            LifeParameters.END_TIDAL_CARBON_DIOXIDE
    )

    /**
     * A factory method for a simulated end tidal carbon dioxide monitor
     * */
    fun createSimulatedEtCO2Monitor() = SimulatedMonitor(
            createStaticEtCO2Monitor(),
            GenerationStrategies.DoubleSinusoidGeneration(0.0, 50.0),
            100)
}