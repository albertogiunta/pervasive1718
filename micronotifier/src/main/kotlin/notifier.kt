import config.ConfigLoader
import config.Services
import controller.CoreController
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import utils.acronymWithSession

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    val core = CoreController.singleton()

    println(core.topics.activeTopics().map {it.acronymWithSession(args) }.toList())

    val amqp = AMQPClient(core.topics.activeTopics().map { it to it.acronymWithSession(args) }.toMap())

    val publishSubjects = core.topics.activeTopics().map {
        it to core.subjects.getSubjectsOf<String>(it.toString())!!
    }.toMap()

    amqp.publishOn(publishSubjects)

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.root())

    if (args.isNotEmpty()) {
        waitInitAndNotifyToMicroSession(Services.NOTIFIER.executableName, Services.instanceId())
    }
}