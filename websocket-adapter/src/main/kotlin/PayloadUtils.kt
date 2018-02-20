import java.lang.ClassCastException
import java.time.ZonedDateTime

interface Payload<T, D> {

    val sid: Long // Session Payload
    val subject: T
    val body: D
    val time: String

    companion object {
        fun getTime(): String = ZonedDateTime.now().toString()
    }
}

inline fun <reified X> PayloadWrapper.objectify(json: String): X {
    val bundle = this.subject.objectifier(json)
    if (bundle is X) {
        return bundle
    } else throw ClassCastException("Class Cast Error")
}

data class PayloadWrapper(override val sid: Long,
                          override val subject: SessionOperation,
                          override val body: String,
                          override val time: String = Payload.getTime()) :
    Payload<SessionOperation, String>

data class SessionOperation(val commandName: String, val path: String, val objectifier: (String) -> Any)
