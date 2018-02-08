package model

import LifeParameters
import com.google.gson.GsonBuilder
import logic.Member
import logic.SessionOperation
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

enum class SubscriptionOperation { SUBSCRIBE, UNSUBSCRIBE, REMOVE }

data class Notification(override val sid: Long,
                        override val topic: LifeParameters,
                        override val body: String,
                        override val time: String = Payload.getTime()) :
        Payload<LifeParameters, String>

data class Update(override val sid: Long,
                  override val topic: LifeParameters,
                  override val body: Double,
                  override val time: String = Payload.getTime()) :
        Payload<LifeParameters, Double>

data class Subscription(override val sid: Long,
                        override val topic: SubscriptionOperation,
                        override val body: Pair<Member, List<LifeParameters>>,
                        override val time: String = Payload.getTime()) :
        Payload<SubscriptionOperation, Pair<Member, List<LifeParameters>>>

data class Join(override val sid: Long,
                override val topic: SessionOperation,
                override val body: Member,
                override val time: String = Payload.getTime()) :
        Payload<SessionOperation, Member>

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()
    val json = Notification(Random().nextLong(), LifeParameters.TEMPERATURE, "DEAD").toJson()

    println(gson.fromJson(json, Notification::class.java).toString())
}