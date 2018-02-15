package model

import utils.GsonInitializer
import LifeParameters
import com.google.gson.GsonBuilder
import utils.toJson
import java.lang.ClassCastException
import java.time.ZonedDateTime
import java.util.*

interface Payload<T, D> {

    val sid: Long // Session Payload
    val subject: T
    val body: D
    val time: String

    companion object {
        fun getTime(): String = ZonedDateTime.now().toString()
    }
}

@Suppress("PROTECTED_CALL_FROM_PUBLIC_INLINE")
enum class SessionOperation(val path: String, protected val xxx: (String) -> Any) {

    CLOSE("/close", {GsonInitializer.gson.fromJson(it, model.Subscription::class.java)}),
    SUBSCRIBE("/subscribe", {GsonInitializer.gson.fromJson(it, model.Subscription::class.java)}),
    UPDATE("/update", {GsonInitializer.gson.fromJson(it, model.Update::class.java)}),
    NOTIFY("/notify", {GsonInitializer.gson.fromJson(it, model.Notification::class.java)});

    @Throws(ClassCastException::class)
    inline fun <reified X> objectify(json: String) : X {
        val bundle = xxx(json)
        if (bundle is X){
            return bundle
        } else throw ClassCastException("Class Cast Error")
    }
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

fun main(args: Array<String>) {

    val gson = GsonBuilder().create()
    val sid = Random().nextLong()

    val json = PayloadWrapper(sid, SessionOperation.NOTIFY, Notification(sid, emptySet(), "DEAD").toJson()).toJson()

    println(json)
    val wrapper = gson.fromJson(json, PayloadWrapper::class.java)
    println(wrapper.toString())

    val n : Notification = wrapper.subject.objectify(wrapper.body) as Notification

    val j = n.toJson()

    println(SessionOperation.NOTIFY.objectify<Notification>(j))
}