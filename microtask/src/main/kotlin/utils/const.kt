package utils

import java.sql.Timestamp
import java.util.*

object WSParams

object EmptyTask{
    const val emptyTaskName : String = "empty task"
    const val emptyTaskId : Int = -1
    //TODO play with this fucking things
    val emptyTaskStartTime : Timestamp = Timestamp(Date().time -1000)
    val emptyTaskEndTime : Timestamp = Timestamp(Date().time )
}

object EmptyMember{
    const val emptyMemberId : Int = -2
    const val emptyMemberName: String = "empty member"
}