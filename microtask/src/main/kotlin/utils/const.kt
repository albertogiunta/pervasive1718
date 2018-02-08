package utils

import java.sql.Timestamp
import java.util.*

object EmptyTask{
    const val emptyTaskName : String = "empty task"
    const val emptyTaskId : Int = -1
    val emptyTaskStartTime : Timestamp = Timestamp(Date(0).time )
    val emptyTaskEndTime : Timestamp = Timestamp(Date(1).time )
}

object EmptyMember{
    const val emptyMemberId : Int = -2
    const val emptyMemberName: String = "empty member"
}