import config.ConfigLoader
import config.Services
import controller.CoreController
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import utils.acronymWithSession

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    println(Services.NOTIFIER.wsURI())

    val core = CoreController.singleton()
            .withoutLogging()
            .loadHandlers()

    val amqp = AMQPClient(core.topics.activeTopics().map { it to it.acronymWithSession(args) }.toMap())

    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

    publishSubjects.forEach { println(it) }

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.root())

    if (Services.isNotStartedIndependently()) {
        waitInitAndNotifyToMicroSession(Services.NOTIFIER.executableName, Services.instanceId())
    }
}