package utils

import logic.Status
import java.sql.Timestamp
import java.util.*

object EmptyTask{
    val emptyTaskId: Int = -1
    val emptyTaskOperatorId: Int = -1
    val emptyTaskStatusId: Int = Status.EMPTY.id
    val emptyTaskActivityId: Int = -1
    val emptyTaskStartTime : Timestamp = Timestamp(Date(0).time )
    val emptyTaskEndTime : Timestamp = Timestamp(Date(1).time )
}

object EmptyMember{
    const val emptyMemberId : Int = -2
    const val emptyMemberName: String = "empty member"
}