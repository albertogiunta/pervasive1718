fun main(argv: Array<String>) {
    val tempMonitor = TemperatureMonitorsFactory.createSimulatedTemperatureMonitor()

    val systolicMonitor = HeartMonitorsFactory.createSimulatedSystolicPressureMonitor()
    val diastolicMonitor = HeartMonitorsFactory.createSimulatedDiastolicPressureMonitor()
    val heartRateMonitor = HeartMonitorsFactory.createSimulatedHeartRateMonitor()

    val spO2Monitor = SpO2MonitorsFactory.createSimulatedSpO2Monitor()
    val etCO2 = EtCO2MonitorsFactory.createSimulatedEtCO2Monitor()

    //BrokerConnector.init("127.0.0.1")
    BrokerConnector.init()
    val pub = RabbitMQPublisher(BrokerConnector.INSTANCE)

    val obsTempGen = ObservableMonitor<Double>(tempMonitor)
    obsTempGen.createObservable(2000).subscribe({
        pub.publish("Dato Temperatura simulato " + it, LifeParameters.TEMPERATURE)
    })

    val obsSysGen = ObservableMonitor<Double>(systolicMonitor)
    obsSysGen.createObservable(2000).subscribe({
        pub.publish("Dato Systolic blood pressure simulato " + it, LifeParameters.SYSTOLIC_BLOOD_PRESSURE)
    })

    val obsDiaGen = ObservableMonitor<Double>(diastolicMonitor)
    obsDiaGen.createObservable(2000).subscribe({
        pub.publish("Dato Diastolic blood pressure simulato " + it, LifeParameters.DIASTOLIC_BLOOD_PRESSURE)
    })

    val obsHrGen = ObservableMonitor<Double>(heartRateMonitor)
    obsHrGen.createObservable(2000).subscribe({
        pub.publish("Dato Heart rate simulato " + it, LifeParameters.HEART_RATE)
    })

    val obsSpO2Gen = ObservableMonitor(spO2Monitor)
    obsSpO2Gen.createObservable(2000).subscribe({
        pub.publish("Dato SpO2 simulato " + it, LifeParameters.OXYGEN_SATURATION)
    })

    val obsEtCO2Gen = ObservableMonitor(etCO2)
    obsSpO2Gen.createObservable(2000).subscribe({
        pub.publish("Dato EtCO2 simulato " + it, LifeParameters.END_TIDAL_CARBON_DIOXIDE)
    })
}
