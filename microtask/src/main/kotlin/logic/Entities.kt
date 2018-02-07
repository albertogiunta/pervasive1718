package logic

import KlaxonDate
import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import dateConverter
import logic.Serializer.gson
import java.sql.Timestamp

data class Task(val id: Int, val name: String, var status: Status, @KlaxonDate val startTime: Timestamp, @KlaxonDate val endTime: Timestamp)

data class Member(val id: Int, val name: String)

interface TaskMemberAssociation {

    val task: Task
    val member: Member

    companion object {
        fun create(task: Task, member: Member) = AssociationImpl(task, member)
    }
}

data class AssociationImpl(override val task: Task, override val member: Member) : TaskMemberAssociation

data class TaskPayload(val doctor: Member, val operation: Operation, val task: Task)

fun TaskPayload.toJson(): String = gson.toJson(this)

object Serializer {

    val gson = Gson()

    val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)

}