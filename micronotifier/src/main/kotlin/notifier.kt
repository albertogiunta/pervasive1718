import config.ConfigLoader
import config.Services
import controller.CoreController
import networking.rabbit.AMQPClient
import networking.ws.RelayService
import spark.Spark
import utils.Logger
import utils.acronymWithSession

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    println(Services.NOTIFIER.wsURI())

    Logger.setLogger(Services.NOTIFIER.wsPath)

    val core = CoreController.singleton().withoutLogging()

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.root())

    Spark.awaitInitialization()

    val topics = core.topics.activeTopics().map { it to it.acronymWithSession(args) }.toMap()
    val amqp = AMQPClient(topics)

    amqp.addObserver(core)

    core.loadHandlers()

    amqp.startPublishing()

    if (Services.isNotStartedIndependently()) {
        waitInitAndNotifyToMicroSession(Services.NOTIFIER.executableName, Services.instanceId())
    }
}