fun main(argv: Array<String>) {
    var tempMonitor = TemperatureMonitorsFactory.createSimulatedTemperatureMonitor()

    var systolicMonitor = HeartMonitorsFactory.createSimulatedSystolicPressureMonitor()
    var diastolicMonitor = HeartMonitorsFactory.createSimulatedDiastolicPressureMonitor()
    var heartRateMonitor = HeartMonitorsFactory.createSimulatedHeartRateMonitor()

    var spO2Monitor = SpO2MonitorsFactory.createSimulatedSpO2Monitor()
    var etCO2 = EtCO2MonitorsFactory.createSimulatedEtCO2Monitor()

    //BrokerConnector.init("127.0.0.1")
    BrokerConnector.init()
    val pub = RabbitMQPublisher(BrokerConnector.INSTANCE)

    val obsTempGen = ObservableMonitor<Double>(tempMonitor, 1000)
    obsTempGen.createObservable(2000).subscribe({
        pub.publish("Dato Temperatura simulato " + it, LifeParameters.TEMPERATURE)
    })

    val obsSysGen = ObservableMonitor<Double>(systolicMonitor, 1000)
    obsSysGen.createObservable(2000).subscribe({
        pub.publish("Dato Systolic blood pressure simulato " + it, LifeParameters.SYSTOLIC_BLOOD_PRESSURE)
    })

    val obsDiaGen = ObservableMonitor<Double>(diastolicMonitor, 1000)
    obsDiaGen.createObservable(2000).subscribe({
        pub.publish("Dato Diastolic blood pressure simulato " + it, LifeParameters.DIASTOLIC_BLOOD_PRESSURE)
    })

    val obsHrGen = ObservableMonitor<Double>(heartRateMonitor, 1000)
    obsHrGen.createObservable(2000).subscribe({
        pub.publish("Dato Heart rate simulato " + it, LifeParameters.TEMPERATURE)
    })

    val obsSpO2Gen = ObservableMonitor(spO2Monitor, 1000)
    obsSpO2Gen.createObservable(2000).subscribe({
        pub.publish("Dato SpO2 simulato " + it, LifeParameters.OXYGEN_SATURATION)
    })


    val obsEtCO2Gen = ObservableMonitor(etCO2, 1000)
    obsSpO2Gen.createObservable(2000).subscribe({
        pub.publish("Dato EtCO2 simulato " + it, LifeParameters.END_TIDAL_CARBON_DIOXIDE)
    })

}
