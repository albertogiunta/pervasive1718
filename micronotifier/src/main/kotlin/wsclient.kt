import config.ConfigLoader
import config.Services
import model.Member
import model.PayloadWrapper
import model.SessionOperation
import model.Subscription
import utils.toJson

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    val member = Member(666, "Mario Rossi")

    val client = WSClient(Services.NOTIFIER.wsURI())

    client.connectBlocking()

    val msg1 = PayloadWrapper(-1L, SessionOperation.SUBSCRIBE,
            Subscription(-1L, member, LifeParameters.values().toList()).toJson()
        ).toJson()

    client.sendMessage(msg1)

    Thread.sleep(5000L)

    val msg2 = PayloadWrapper(-1L, SessionOperation.CLOSE, member.toJson()).toJson()

    client.sendMessage(msg2)

    client.closeBlocking()
}