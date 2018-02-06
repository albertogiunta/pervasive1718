object HeartMonitorsFactory {

    /**
     * A factory method for a static systolic pressure monitor
     * */
    fun createStaticSystolicBloodPressureMonitor() = BasicMonitor(
            120.0,
            LifeParameters.SYSTOLIC_BLOOD_PRESSURE.longName,
            LifeParameters.SYSTOLIC_BLOOD_PRESSURE
    )


    /**
     * A factory method for a static diastolic blood pressure monitor
     * */
    fun createStaticDiastolicBloodPressureMonitor() = BasicMonitor(
            80.0,
            LifeParameters.DIASTOLIC_BLOOD_PRESSURE.longName,
            LifeParameters.DIASTOLIC_BLOOD_PRESSURE
    )


    /**
     * A factory method for a static heart rate monitor
     * */
    fun createStaticHeartRateMonitor() = BasicMonitor(
            70.0,
            LifeParameters.HEART_RATE.longName,
            LifeParameters.HEART_RATE
    )

    /**
     * A factory method for a simulated systolic blood pressure monitor
     * */
    fun createSimulatedSystolicPressureMonitor() = SimulatedMonitor(
            createStaticSystolicBloodPressureMonitor(),
            GenerationStrategies.DoubleSinusoidGeneration(70.0, 120.0),
            100)

    /**
     * A factory method for a simulated diastolic blood pressure monitor
     * */
    fun createSimulatedDiastolicPressureMonitor() = SimulatedMonitor(
            createStaticDiastolicBloodPressureMonitor(),
            GenerationStrategies.DoubleSinusoidGeneration(40.0, 100.0),
            100)


    /**
     * A factory method for a simulated heart rate monitor
     * */
    fun createSimulatedHeartRateMonitor() = SimulatedMonitor(
            createStaticHeartRateMonitor(),
            GenerationStrategies.DoubleSinusoidGeneration(0.0, 200.0),
            100)
}