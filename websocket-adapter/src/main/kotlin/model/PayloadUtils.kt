package model

import utils.GsonInitializer
import java.lang.ClassCastException
import java.time.ZonedDateTime


interface Payload<T, D> {

    val sid: Long // Session model.Payload
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

    // TASKS
    ADD_LEADER({ GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) }),
    ADD_MEMBER({ GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) }),
    ADD_TASK({ GsonInitializer.fromJson(it, TaskAssignment::class.java) }),
    REMOVE_TASK({ GsonInitializer.fromJson(it, TaskAssignment::class.java) }),
    CHANGE_TASK_STATUS({ GsonInitializer.fromJson(it, TaskAssignment::class.java) }),
    ERROR_REMOVING_TASK({ GsonInitializer.fromJson(it, TaskError::class.java) }),
    ERROR_CHANGING_STATUS({ GsonInitializer.fromJson(it, StatusError::class.java) });
}

data class Notification(val lifeParameter: LifeParameters, val boundaries: List<Boundary>)

data class Update(val lifeParameter: LifeParameters, val value: Double)

data class Subscription(val subject: Member, val topics: List<LifeParameters>)

data class TaskAssignment(val member: Member, val task: Task)

data class MembersAdditionNotification(val members: List<Member>)

data class TaskError(val task: Task, val error: String)

data class StatusError(val statusId: Int, val task: Task, val error: String)
