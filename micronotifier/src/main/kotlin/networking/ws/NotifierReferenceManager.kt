package networking.ws

import org.eclipse.jetty.websocket.api.annotations.WebSocket

interface ReferenceManager<N, R> {
    operator fun <T : R> set(refId : N, refObj : T) : R?
    operator fun <T : R> get(refId: N) : T?
    operator fun <T : R> minus(refId: N) : T?
    operator fun contains(refId: N) : Boolean
}

@Suppress("UNCHECKED_CAST")
object NotifierReferenceManager : ReferenceManager<String, @WebSocket Any> {

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