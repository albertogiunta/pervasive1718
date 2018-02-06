object TemperatureMonitorsFactory {

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
            GenerationStrategies.DoubleSinusoidGeneration(0.0, 30.0),
            100)
}