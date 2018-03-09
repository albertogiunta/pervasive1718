package networking.ws

import WSServerInitializer
import config.ConfigLoader
import config.Services
import spark.Spark

interface ReferenceManager<N, R> {
    operator fun <T : R> set(refId : N, refObj : T) : R?
    operator fun <T : R> get(refId: N) : T?
    operator fun <T : R> minus(refId: N) : T?
    operator fun contains(refId: N) : Boolean
}

@Suppress("UNCHECKED_CAST")
object NotifierReferenceManager : ReferenceManager<String, Any> {

    private val wsReferences = mutableMapOf<String, Any>()

    @Synchronized
    override fun <T : Any> set(refId: String, refObj: T): Any? = wsReferences.put(refId, refObj)

    @Synchronized
    override fun <T : Any> get(refId: String): T? = wsReferences[refId] as T?

    @Synchronized
    override fun <T : Any> minus(refId: String): T? = wsReferences.remove(refId) as T?

    @Synchronized
    override fun contains(refId: String): Boolean = wsReferences.contains(refId)

}

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    WSServerInitializer.init(RelayService::class.java, Services.NOTIFIER.port, Services.NOTIFIER.root())

    val ws : () -> RelayService? = {NotifierReferenceManager[RelayService::class.java.name]}

    Spark.awaitInitialization()

    (0 until 20).forEach {
        println(ws()?.name)
        Thread.sleep(100L)
    }

}