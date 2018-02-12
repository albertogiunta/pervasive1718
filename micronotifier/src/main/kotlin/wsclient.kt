import logic.Member
import model.PayloadWrapper
import model.SessionOperation
import model.Subscription

fun main(args: Array<String>) {

    println(URIFactory.getNotifierURI())

    val client = WSClientInitializer.init(WSClient(URIFactory.getNotifierURI()))

    Thread.sleep(5000L)

    val msg1 = PayloadWrapper(-1L, SessionOperation.SUBSCRIBE,
            Subscription(-1L, Member(666, "Mario Rossi"), listOf(LifeParameters.TEMPERATURE)).toJson()
        ).toJson()

    client.sendMessage(msg1)

    Thread.sleep(5000L)

    val msg2 = PayloadWrapper(-1L, SessionOperation.CLOSE,
            Subscription(-1L, Member(666, "Mario Rossi"), emptyList()).toJson()
        ).toJson()

    client.sendMessage(msg2)

    client.close()
}