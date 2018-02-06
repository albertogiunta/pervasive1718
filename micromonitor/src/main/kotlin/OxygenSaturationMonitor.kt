object SpO2MonitorsFactory {

    /**
     * A factory method for a static Oxygen saturation Monitor
     * */
    fun createStaticSpO2Monitor() = BasicMonitor(
            80,
            LifeParameters.OXYGEN_SATURATION.longName,
            LifeParameters.OXYGEN_SATURATION
    )

    /**
     * A factory method for a simulated oxygen saturation monitor
     * */
    fun createSimulatedSpO2Monitor() = SimulatedMonitor(
            createStaticSpO2Monitor(),
            GenerationStrategies.IntSinusoidGeneration(0, 100),
            100)
}