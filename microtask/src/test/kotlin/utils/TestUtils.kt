package utils

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import model.Member
import model.Task
import model.TaskOperation
import model.TaskPayload
import java.io.StringReader
import java.util.ArrayList
import WSClient

fun addLeaderThread(memberId: Int): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient()
                .sendMessage(TaskPayload(Member(memberId, "Leader"), TaskOperation.ADD_LEADER, Task.emptyTask()).toJson())
    })
}
fun addMemberThread(memberId: Int): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient()
                .sendMessage(TaskPayload(Member(memberId, "Member"), TaskOperation.ADD_MEMBER, Task.emptyTask()).toJson())
    })
}

fun addTaskThread(task: Task, member: Member): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().sendMessage(TaskPayload(member, TaskOperation.ADD_TASK, task).toJson())
    })
}

fun removeTaskThread(task: Task): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().sendMessage(TaskPayload(Member.emptyMember(), TaskOperation.REMOVE_TASK, task).toJson())
    })
}

fun changeTaskStatus(task: Task): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().sendMessage(TaskPayload(Member.emptyMember(), TaskOperation.CHANGE_TASK_STATUS, task).toJson())
    })
}


fun initializeConnectionWithTaskWSClient(): WSClient {
    return WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
}