import com.google.gson.GsonBuilder
import controller.CoreController
import logic.Member
import networking.rabbit.AMQPClient
import utils.Logger

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()

    CoreController.init(LifeParameters.values().toSet())
    val core = CoreController.singleton()

//    WSServerInitializer.init(RelayService::class.java, WSParams.WS_PORT, WSParams.WS_PATH_NOTIFIER)

    BrokerConnector.init()
    val amqp = AMQPClient(BrokerConnector.INSTANCE, core.topics.activeTopics())
    core.topics.activeTopics().forEach { core.subjects.createNewSubjectFor(it.toString()) }

    val publishSubjects = core.topics.activeTopics().map { it to core.subjects.getSubjectsOf(it.toString()) }.toMap()

    amqp.publishOn(publishSubjects)

    // This should be placed into the WS Class
    // Check OUT OF BOUND Heath Parameters
    with(publishSubjects) {
        this.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, Pair::class.java).run {
                    LifeParameters.valueOf(this.first.toString()) to this.second.toString().toDouble()
                }
            }.filter {
                // Check if out of boundaries and notify of the WS
                        false
                    }.doOnNext {
                        Logger.info(it.toString())
                    }.subscribe { message ->
                        // Do Stuff, if necessary but Subscription is MANDATORY.
                        core.topics[topic]?.forEach { member ->
                            core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                }
            }
        }
    }

    // This should be pushed into the WS Class
    // Simple relays received Health Values to Listeners
    with(publishSubjects) {
        this.forEach { topic, subject ->
            subject.map {
                gson.fromJson(it, Pair::class.java).run {
                    LifeParameters.valueOf(this.first.toString()) to this.second.toString().toDouble()
                }
            }.doOnNext {
                        Logger.info(it.toString())
                    }.subscribe { message ->
                        // Do stuff with the WebSockets, dispatch only some of the merged values
                        // With one are specified into controller.listenerMap: Member -> Set<LifeParameters>
                        core.topics[topic]?.forEach { member ->
                            Logger.info("$member ===> ${message.toJson()}")
                            core.sessions[member]?.remote?.sendString(message.toJson()) // Notify the WS, dunno how.
                        }
                    }
        }
    }

    core.topics.add(LifeParameters.HEART_RATE, Member(666, "Mario Rossi"))
    core.topics.add(LifeParameters.TEMPERATURE, Member(666, "Mario Rossi"))
    core.topics.add(LifeParameters.OXYGEN_SATURATION, Member(777, "Padre Pio"))
}

