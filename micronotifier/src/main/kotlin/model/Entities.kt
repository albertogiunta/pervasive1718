package model

import LifeParameters
import com.google.gson.GsonBuilder
import logic.Member
import toJson
import java.time.ZonedDateTime
import java.util.*

interface Payload<T, D> {

    val sid: Long // Session Payload
    val topic: T
    val body: D
    val time: String

    companion object {
        fun getTime(): String = ZonedDateTime.now().toString()
    }
}

enum class SessionOperation(val path: String) {
    OPEN("/open"),
    CLOSE("/close"),
    ADD("/add"),
    REMOVE("/remove"),
    SUBSCRIBE("/subscribe"),
    UNSUBSCRIBE("/unsubscribe"),
    UPDATE("/update"),
    NOTIFY("/notify")
}

data class PayloadWrapper(override val sid: Long,
                          override val topic: SessionOperation,
                          override val body: String,
                          override val time: String = Payload.getTime()) :
        Payload<SessionOperation, String>


data class Notification(override val sid: Long,
                        override val topic: Set<LifeParameters>,
                        override val body: String,
                        override val time: String = Payload.getTime()) :
        Payload<Set<LifeParameters>, String>

data class Update(override val sid: Long,
                  override val topic: LifeParameters,
                  override val body: Double,
                  override val time: String = Payload.getTime()) :
        Payload<LifeParameters, Double>

data class Subscription(override val sid: Long,
                        override val topic: Member,
                        override val body: List<LifeParameters>,
                        override val time: String = Payload.getTime()) :
        Payload<Member, List<LifeParameters>>

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()
    val sid = Random().nextLong()

    val json = PayloadWrapper(sid, SessionOperation.NOTIFY, Notification(sid, emptySet(), "DEAD").toJson()).toJson()

    println(json)
    val wrapper = gson.fromJson(json, PayloadWrapper::class.java)
    println(wrapper.toString())
    println(gson.fromJson(wrapper.body, Notification::class.java))
}