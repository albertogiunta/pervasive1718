package logic

import KlaxonDate
import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import dateConverter
import logic.Serializer.gson
import utils.EmptyMember
import utils.EmptyTask
import java.sql.Timestamp
import java.util.*

data class Task(val id: Int, val name: String, var status: Status, @KlaxonDate val startTime: Timestamp, @KlaxonDate val endTime: Timestamp)

fun emptyTask():Task = Task(EmptyTask.emptyTaskId,EmptyTask.emptyTaskName,Status.EMPTY,EmptyTask.emptyTaskStartTime,EmptyTask.emptyTaskEndTime )
fun defaultTask():Task = Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))

data class Member(val id: Int, val name: String)

fun emptyMember():Member = Member(EmptyMember.emptyMemberId, EmptyMember.emptyMemberName)
fun defaultMember():Member = Member(1, "Member")

interface TaskMemberAssociation {

    val task: Task
    val member: Member

    companion object {
        fun create(task: Task, member: Member) = AssociationImpl(task, member)
    }
}

data class AssociationImpl(override val task: Task, override val member: Member) : TaskMemberAssociation

data class TaskPayload(val member: Member, val taskOperation: TaskOperation, val task: Task)

data class SessionPayload(val leader: Member, val sessionOperation: SessionOperation, val sessionId: Int)

fun TaskPayload.toJson(): String = gson.toJson(this)

object Serializer {

    val gson = Gson()

    val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)

}