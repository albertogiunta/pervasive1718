import config.Services
import controller.CoreController
import networking.rabbit.AMQPClient
import networking.ws.RelayService

fun main(args: Array<String>) {

    val core = CoreController.singleton()

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.wsPath)

    BrokerConnector.init()
    val amqp = AMQPClient(BrokerConnector.INSTANCE, core.topics.activeTopics())

    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

}

