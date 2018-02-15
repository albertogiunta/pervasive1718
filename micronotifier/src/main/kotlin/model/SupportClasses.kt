package model

import utils.GsonInitializer
import LifeParameters
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

enum class SessionOperation(val path: String, private val xxx: (String) -> Any) {

    CLOSE("/close", { GsonInitializer.gson.fromJson(it, model.Member::class.java)}),
    SUBSCRIBE("/subscribe", { GsonInitializer.gson.fromJson(it, model.Subscription::class.java)}),
    UPDATE("/update", { GsonInitializer.gson.fromJson(it, model.Update::class.java)}),
    NOTIFY("/notify", { GsonInitializer.gson.fromJson(it, model.Notification::class.java)});

    fun objectify(json: String) : Any = xxx(json)
}

data class PayloadWrapper(override val sid: Long,
                          override val subject: SessionOperation,
                          override val body: String,
                          override val time: String = Payload.getTime()) :
        Payload<SessionOperation, String>


data class Notification(override val sid: Long,
                        override val subject: Set<LifeParameters>,
                        override val body: String,
                        override val time: String = Payload.getTime()) :
        Payload<Set<LifeParameters>, String>

data class Update(override val sid: Long,
                  override val subject: LifeParameters,
                  override val body: Double,
                  override val time: String = Payload.getTime()) :
        Payload<LifeParameters, Double>

data class Subscription(override val sid: Long,
                        override val subject: Member,
                        override val body: List<LifeParameters>,
                        override val time: String = Payload.getTime()) :
        Payload<Member, List<LifeParameters>>