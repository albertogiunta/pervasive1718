package model

import SessionOperation
import utils.GsonInitializer

interface TaskMemberAssociation {

    val task: Task
    val member: Member

    companion object {
        fun create(task: Task, member: Member) = AssociationImpl(task, member)
    }
}

data class AssociationImpl(override val task: Task, override val member: Member) : TaskMemberAssociation

data class TaskPayload(val member: Member, val taskOperation: TaskOperation, val task: Task)

object TaskOperations {
    val ADD_LEADER = SessionOperation("ADD_LEADER", "/add_leader", { GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) })
    val ADD_MEMBER = SessionOperation("ADD_MEMBER", "/add_member", { GsonInitializer.fromJson(it, MembersAdditionNotification::class.java) })
    val ADD_TASK = SessionOperation("ADD_TASK", "/add_task", { GsonInitializer.fromJson(it, TaskAssignment::class.java) })
    val REMOVE_TASK = SessionOperation("REMOVE_TASK", "/remove_task", { GsonInitializer.fromJson(it, TaskAssignment::class.java) })
    val CHANGE_TASK_STATUS = SessionOperation("CHANGE_TASK_STATUS", "/change_task_status", { GsonInitializer.fromJson(it, TaskAssignment::class.java) })
    val ERROR_REMOVING_TASK = SessionOperation("ERROR_REMOVING_TASK", "/error_removing_task", { })
    val ERROR_CHANGING_STATUS = SessionOperation("ERROR_CHANGING_STATUS", "/error_changing_status", { })
}

data class TaskAssignment(val member: Member, val task: Task)

data class MembersAdditionNotification(val body: List<Member>)