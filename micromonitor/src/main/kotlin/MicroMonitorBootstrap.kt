import config.ConfigLoader
import utils.acronymWithSession

object MicroMonitorBootstrap {

    @JvmStatic
    fun main(argv: Array<String>) {
        ConfigLoader().load()

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

        val obsTempGen = ObservableMonitor(tempMonitor)
        obsTempGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsTempGen.measuredParameter.acronymWithSession(argv))
        })

        val obsSysGen = ObservableMonitor(systolicMonitor)
        obsSysGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsSysGen.measuredParameter.acronymWithSession(argv))
        })

        val obsDiaGen = ObservableMonitor(diastolicMonitor)
        obsDiaGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsDiaGen.measuredParameter.acronymWithSession(argv))
        })

        val obsHrGen = ObservableMonitor(heartRateMonitor)
        obsHrGen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsHrGen.measuredParameter.acronymWithSession(argv))
        })

        val obsSpO2Gen = ObservableMonitor(spO2Monitor)
        obsSpO2Gen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsSpO2Gen.measuredParameter.acronymWithSession(argv))
        })

        val obsEtCO2Gen = ObservableMonitor(etCO2)
        obsEtCO2Gen.createObservable(OBSERVATION_REFRESH_TIME).subscribe({
            pub.publish("" + it, obsEtCO2Gen.measuredParameter.acronymWithSession(argv))
        })
    }

}