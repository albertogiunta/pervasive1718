import config.ConfigLoader
import config.Services
import utils.acronymWithPort
import utils.calculatePort

object MicroMonitorMain {

    @JvmStatic
    fun main(argv: Array<String>) {
        ConfigLoader().load()

        val port = Services.MONITOR.calculatePort(argv)

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
            pub.publish("" + it, obsTempGen.measuredParameter.acronymWithPort(port))
        })

        val obsSysGen = ObservableMonitor<Double>(systolicMonitor)
        obsSysGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsSysGen.measuredParameter.acronymWithPort(port))
        })

        val obsDiaGen = ObservableMonitor<Double>(diastolicMonitor)
        obsDiaGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsDiaGen.measuredParameter.acronymWithPort(port))
        })

        val obsHrGen = ObservableMonitor<Double>(heartRateMonitor)
        obsHrGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsHrGen.measuredParameter.acronymWithPort(port))
        })

        val obsSpO2Gen = ObservableMonitor(spO2Monitor)
        obsSpO2Gen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsSpO2Gen.measuredParameter.acronymWithPort(port))
        })

        val obsEtCO2Gen = ObservableMonitor(etCO2)
        obsEtCO2Gen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsEtCO2Gen.measuredParameter.acronymWithPort(port))
        })
    }

}