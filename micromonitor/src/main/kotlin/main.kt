
fun main(argv: Array<String>) {

    val OBSERVATION_REFRESH_TIME = 1000L

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
    obsTempGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
        pub.publish("" + it, obsTempGen.measuredParameter)
    })

    val obsSysGen = ObservableMonitor<Double>(systolicMonitor)
    obsSysGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
        pub.publish("" + it, obsSysGen.measuredParameter)
    })

    val obsDiaGen = ObservableMonitor<Double>(diastolicMonitor)
    obsDiaGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
        pub.publish("" + it, obsDiaGen.measuredParameter)
    })

    val obsHrGen = ObservableMonitor<Double>(heartRateMonitor)
    obsHrGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
        pub.publish("" + it, obsHrGen.measuredParameter)
    })

    val obsSpO2Gen = ObservableMonitor(spO2Monitor)
    obsSpO2Gen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
        pub.publish("" + it, obsSpO2Gen.measuredParameter)
    })

    val obsEtCO2Gen = ObservableMonitor(etCO2)
    obsEtCO2Gen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
        pub.publish("" + it, obsEtCO2Gen.measuredParameter)
    })
}
