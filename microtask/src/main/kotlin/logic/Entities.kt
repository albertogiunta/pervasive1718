package logic

import KlaxonDate
import com.beust.klaxon.Klaxon
import dateConverter
import utils.EmptyMember
import utils.EmptyTask
import java.sql.Timestamp
import java.util.*

data class VisibleTask(val id: Int, val name: String, val priority: Priority, val operatorId: Int, val operatorName: String, val operatorSurname: String)

enum class Priority { HIGH, LOW }

data class Task @JvmOverloads constructor(var id: Int = 0, var operatorId: Int, @KlaxonDate var startTime: Timestamp, @KlaxonDate var endTime: Timestamp, var activityId: Int, var statusId: Int) {
    companion object {
        fun emptyTask(): Task =
            Task(EmptyTask.emptyTaskId,EmptyTask.emptyTaskOperatorId, EmptyTask.emptyTaskStartTime, EmptyTask.emptyTaskEndTime, EmptyTask.emptyTaskActivityId, EmptyTask.emptyTaskStatusId)

        fun defaultTask(): Task =
            Task(1, 1, Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)
    }
}

fun Task.toVisibleTask(member: Member) =
    VisibleTask(this.id, "il name del task va tolto", Priority.HIGH, member.id, member.name, member.name)

data class Member(val id: Int, val name: String) {
    companion object {
        fun emptyMember(): Member = Member(EmptyMember.emptyMemberId, EmptyMember.emptyMemberName)

        fun defaultMember(): Member = Member(-52, "Member")
    }
}

interface TaskMemberAssociation {

    val task: Task
    val member: Member

    companion object {
        fun create(task: Task, member: Member) = AssociationImpl(task, member)
    }
}

data class AssociationImpl(override val task: Task, override val member: Member) : TaskMemberAssociation

data class TaskPayload(val member: Member, val taskOperation: TaskOperation, val task: Task)

object Serializer {

    val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)

}