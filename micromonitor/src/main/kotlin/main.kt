fun main(argv: Array<String>) {
    var mon = TemperatureMonitor()
    var gen = SimulatedMonitor(mon, GenerationStrategies.SinusoidGeneration(), 100)

    /*(1 until 10).forEach({
        println(gen.currentValue())
        Thread.sleep(1000)
    })*/

    //BrokerConnector.init("127.0.0.1")
    BrokerConnector.init()
    val pub = RabbitMQPublisher(BrokerConnector.INSTANCE)

    val obsGen = ObservableMonitor<Double>(gen, 1000)
    obsGen.createObservable(2000).subscribe({ pub.publish("Dato Temperatura simulato " + it, LifeParameters.TEMPERATURE) })

}
