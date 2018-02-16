import config.ConfigLoader
import config.Services
import controller.CoreController
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import utils.acronymWithSession

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    val core = CoreController.singleton()

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.wsPath)

    BrokerConnector.init(LifeParameters.values().map { it.acronymWithSession(args) }.toList())
    val amqp = AMQPClient(
            BrokerConnector.INSTANCE,
            core.topics.activeTopics().map {
                it to it.acronymWithSession(args)
            }.toMap())

    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

}