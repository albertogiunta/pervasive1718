package model

import LifeParameters
import Payload
import PayloadWrapper
import SessionOperation
import com.google.gson.GsonBuilder
import objectify
import utils.GsonInitializer
import utils.toJson
import java.util.*

object SessionOperations {
    val CLOSE = SessionOperation("CLOSE", "/close", { GsonInitializer.fromJson(it, model.Member::class.java) })
    val SUBSCRIBE = SessionOperation("SUBSCRIBE", "/subscribe", { GsonInitializer.fromJson(it, model.Subscription::class.java) })
    val UPDATE = SessionOperation("UPDATE", "/update", { GsonInitializer.fromJson(it, model.Update::class.java) })
    val NOTIFY = SessionOperation("NOTIFY", "/notify", { GsonInitializer.fromJson(it, model.Notification::class.java) })
}

data class Notification(override val sid: Long,
                        override val subject: LifeParameters,
                        override val body: List<Boundary>,
                        override val time: String = Payload.getTime()) :
    Payload<LifeParameters, List<Boundary>>

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

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()
    val sid = Random().nextLong()

    val json = PayloadWrapper(sid, SessionOperations.NOTIFY,
        Notification(sid, LifeParameters.HEART_RATE, emptyList()).toJson()
    ).toJson()

    println(json)
    val wrapper = gson.fromJson(json, PayloadWrapper::class.java)
    println(wrapper.toString())

    val n: Notification = wrapper.objectify(wrapper.body) as Notification

    val j = n.toJson()

    println(SessionOperations.NOTIFY.objectifier(j))
}