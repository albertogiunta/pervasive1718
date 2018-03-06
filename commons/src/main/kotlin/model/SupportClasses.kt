package model

import com.beust.klaxon.Klaxon
import utils.KlaxonDate
import utils.dateConverter
import java.sql.Timestamp
import java.util.*

object EmptyTask{
    const val emptyTaskId: Int = -1
    const val emptyTaskName: String = "Task"
    const val emptyTaskOperatorId: String = "cf a caso"
    val emptyTaskStatusId: Int = Status.EMPTY.id
    const val emptyTaskActivityId: Int = -1
    val emptyTaskStartTime : Timestamp = Timestamp(Date(0).time )
    val emptyTaskEndTime : Timestamp = Timestamp(Date(1000).time )
    const val emptySessionId = -1
}

object EmptyMember{
    const val emptyMemberId : Int = -2
    const val emptyMemberName: String = "empty member"
}

object Serializer {
    val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
}