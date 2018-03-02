package model

import utils.GsonInitializer
import java.lang.ClassCastException
import java.sql.Timestamp
import java.util.*


interface Payload<T, D> {

    val sid: Int // Session model.Payload
    val subject: T
    val body: D
    val time: String

    companion object {
        fun getTime(): String = Timestamp(Date().time).toString()
    }
}

inline fun <reified X> PayloadWrapper.objectify(json: String): X {
    val bundle = this.subject.objectifier(json)
    if (bundle is X) {
        return bundle
    } else throw ClassCastException("Class Cast Error")
}

data class PayloadWrapper(override val sid: Int,
                          override val subject: WSOperations,
                          override val body: String,
                          override val time: String = Payload.getTime()) :
        Payload<WSOperations, String>

//data class model.WSOperations(val commandName: String, val path: String, val objectifier: (String) -> Any)

enum class WSOperations(val objectifier: (String) -> Any) {

    // NOTIFIER
    CLOSE({ GsonInitializer.fromJson(it, Member::class.java) }),
    SUBSCRIBE({ GsonInitializer.fromJson(it, Subscription::class.java) }),
    UPDATE({ GsonInitializer.fromJson(it, Update::class.java) }),
    NOTIFY({ GsonInitializer.fromJson(it, Notification::class.java) }),
    ANSWER({ GsonInitializer.fromJson(it, Response::class.java)}),

    // TASKS
    ADD_LEADER({ GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) }),
    LIST_MEMBERS_REQUEST({ GsonInitializer.fromJson(it, Unit::class.java) }),
    LIST_MEMBERS_RESPONSE({ GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) }),
    LEADER_RESPONSE({ utils.GsonInitializer.fromJson(it,GenericResponse::class.java) }),
    SESSION_HANDLER_ERROR_RESPONSE({ utils.GsonInitializer.fromJson(it,GenericResponse::class.java) }),
    SESSION_HANDLER_RESPONSE({ utils.GsonInitializer.fromJson(it,SessionDNS::class.java) }),
    ADD_MEMBER({ GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) }),

    ADD_TASK({ com.beust.klaxon.Klaxon().fieldConverter(utils.KlaxonDate::class,
            utils.dateConverter).parse<model.TaskAssignment>(it)!! }),

    REMOVE_TASK({ com.beust.klaxon.Klaxon().fieldConverter(utils.KlaxonDate::class,
            utils.dateConverter).parse<model.TaskAssignment>(it)!! }),

    CHANGE_TASK_STATUS({ com.beust.klaxon.Klaxon().fieldConverter(utils.KlaxonDate::class,
            utils.dateConverter).parse<model.TaskAssignment>(it)!! }),

    ERROR_REMOVING_TASK({ com.beust.klaxon.Klaxon().fieldConverter(utils.KlaxonDate::class,
            utils.dateConverter).parse<model.TaskError>(it)!! }),

    ERROR_CHANGING_STATUS({ GsonInitializer.fromJson(it, StatusError::class.java) }),
    ERROR_CREATING_INSTANCE_POOL_FULL({ GsonInitializer.fromJson(it, kotlin.Unit::class.java)}),
    // ACTIVITY
    GET_ALL_ACTIVITIES({ GsonInitializer.fromJson(it, ActivityRequest::class.java) }),
    SET_ALL_ACTIVITIES({ GsonInitializer.fromJson(it, ActivityAdditionNotification::class.java) }),

    // SESSION
    NEW_SESSION({ GsonInitializer.fromJson(it, SessionAssignment::class.java) });
}

data class GenericResponse(val response: String)

data class Notification(val lifeParameter: LifeParameters, val boundaries: List<Boundary>)

data class Update(val lifeParameter: LifeParameters, val value: Double)

data class Response(val code: Int, val toMessage: String)

data class Subscription(val subject: Member, val topics: List<LifeParameters>)

data class TaskAssignment(val member: Member, val task: AugmentedTask)

data class MembersAdditionNotification(val members: List<Member>)

data class AugmentedMembersAdditionNotification(val members: List<AugmentedMemberFromServer>)

data class ActivityRequest(val activityTypeId: Int)

data class ActivityAdditionNotification(val activities: List<Activity>)

data class TaskError(val task: Task, val error: String)

data class StatusError(val statusId: Int, val task: Task, val error: String)

data class SessionAssignment(val patientCF: String, val leaderCF: String)

data class SessionDNS(val sessionId: Int, val patientCF: String, val instanceId: Int = 0, val leaderCF: String)