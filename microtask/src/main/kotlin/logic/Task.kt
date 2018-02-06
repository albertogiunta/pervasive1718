package logic

import logic.ontologies.Status
import java.sql.Timestamp

interface Task{
    val id: Int
    val name: String
    var status: Status
    val startTime: Timestamp
    val endTime: Timestamp

    companion object {
        fun create(id:Int , name:String, status:Status, startTime:Timestamp, endTime:Timestamp): Task = TaskImpl(id,name,status,startTime,endTime)
    }
}


class TaskImpl(override val id:Int , override val name:String, override var status:Status, override val startTime:Timestamp, override val endTime:Timestamp) :Task{

}