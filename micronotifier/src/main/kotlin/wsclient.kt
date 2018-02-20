import config.ConfigLoader
import config.Services
import model.*
import utils.toJson

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    val member = Member(666, "Mario Rossi")

    val client = WSClient(Services.NOTIFIER.wsURI())

    client.connectBlocking()

    val msg1 = PayloadWrapper(-1L, WSOperations.SUBSCRIBE,
            Subscription(member, LifeParameters.values().toList()).toJson()
    ).toJson()

    client.sendMessage(msg1)

    Thread.sleep(5000L)

    val msg2 = PayloadWrapper(-1L, WSOperations.CLOSE, member.toJson()).toJson()

    client.sendMessage(msg2)

    client.closeBlocking()
}