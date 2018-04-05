import model.LifeParameters

object HeartMonitorsFactory {

    const val DEFAULT_SYSTOLIC_BLOOD_PRESSURE_INIT_VAL = 120.0
    const val DEFAULT_DIASTOLIC_BLOOD_PRESSURE_INIT_VAL = 80.0
    const val DEFAULT_HEART_RATE_INIT_VAL = 70.0

    private const val DEFAULT_SYSTOLIC_BLOOD_PRESSURE_MIN_BOUND = 70.0
    private const val DEFAULT_DIASTOLIC_BLOOD_PRESSURE_MIN_BOUND = 40.0
    private const val DEFAULT_HEART_RATE_MIN_BOUND = 0.0

    private const val DEFAULT_SYSTOLIC_BLOOD_PRESSURE_MAX_BOUND = 120.0
    private const val DEFAULT_DIASTOLIC_BLOOD_PRESSURE_MAX_BOUND = 100.0
    private const val DEFAULT_HEART_RATE_MAX_BOUND = 200.0


    private const val DEFAULT_REFRESH_RATE = 3000L


    /**
     * A factory method for a static systolic pressure monitor
     * */
    fun createStaticSystolicBloodPressureMonitor() = BasicMonitor(
            DEFAULT_SYSTOLIC_BLOOD_PRESSURE_INIT_VAL,
            LifeParameters.SYSTOLIC_BLOOD_PRESSURE.longName,
            LifeParameters.SYSTOLIC_BLOOD_PRESSURE
    )


    /**
     * A factory method for a static diastolic blood pressure monitor
     * */
    fun createStaticDiastolicBloodPressureMonitor() = BasicMonitor(
            DEFAULT_DIASTOLIC_BLOOD_PRESSURE_INIT_VAL,
            LifeParameters.DIASTOLIC_BLOOD_PRESSURE.longName,
            LifeParameters.DIASTOLIC_BLOOD_PRESSURE
    )


    /**
     * A factory method for a static heart rate monitor
     * */
    fun createStaticHeartRateMonitor() = BasicMonitor(
            DEFAULT_HEART_RATE_INIT_VAL,
            LifeParameters.HEART_RATE.longName,
            LifeParameters.HEART_RATE
    )

    /**
     * A factory method for a simulated systolic blood pressure monitor
     * */
    fun createSimulatedSystolicPressureMonitor() = SimulatedMonitor(
            createStaticSystolicBloodPressureMonitor(),
            GenerationStrategies.DoubleSinusoidGeneration(
                    DEFAULT_SYSTOLIC_BLOOD_PRESSURE_MIN_BOUND,
                    DEFAULT_SYSTOLIC_BLOOD_PRESSURE_MAX_BOUND),
            DEFAULT_REFRESH_RATE)


    /**
     * A factory method for a simulated diastolic blood pressure monitor
     * */
    fun createSimulatedDiastolicPressureMonitor() = SimulatedMonitor(
            createStaticDiastolicBloodPressureMonitor(),
            GenerationStrategies.DoubleSinusoidGeneration(
                    DEFAULT_DIASTOLIC_BLOOD_PRESSURE_MIN_BOUND,
                    DEFAULT_DIASTOLIC_BLOOD_PRESSURE_MAX_BOUND),
            DEFAULT_REFRESH_RATE)


    /**
     * A factory method for a simulated heart rate monitor
     * */
    fun createSimulatedHeartRateMonitor() = SimulatedMonitor(
            createStaticHeartRateMonitor(),
            GenerationStrategies.DoubleSinusoidGeneration(
                    DEFAULT_HEART_RATE_MIN_BOUND,
                    DEFAULT_HEART_RATE_MAX_BOUND),
            DEFAULT_REFRESH_RATE)
}