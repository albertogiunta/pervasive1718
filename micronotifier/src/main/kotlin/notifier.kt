import config.ConfigLoader
import config.Services
import controller.CoreController
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import utils.acronymWithPort
import utils.calculatePort

fun main(args: Array<String>) {

    ConfigLoader().load()

    val core = CoreController.singleton()

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.wsPath)

    BrokerConnector.init()
    val amqp = AMQPClient(
            BrokerConnector.INSTANCE,
            core.topics.activeTopics().map {
                it to it.acronymWithPort(Services.MONITOR.calculatePort(args))
            }.toMap())

    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

}