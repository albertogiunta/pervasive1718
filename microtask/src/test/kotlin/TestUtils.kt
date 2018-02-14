import logic.Member
import logic.Task
import logic.TaskOperation
import logic.TaskPayload

fun addLeaderThread(memberId: Int): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient()
                .send(TaskPayload(Member(memberId, "Leader"), TaskOperation.ADD_LEADER, Task.emptyTask()).toJson())
    })
}
fun addMemberThread(memberId: Int): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient()
                .send(TaskPayload(Member(memberId, "Member"), TaskOperation.ADD_MEMBER, Task.emptyTask()).toJson())
    })
}

fun addTaskThread(task: Task, member: Member): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().send(TaskPayload(member, TaskOperation.ADD_TASK, task).toJson())
    })
}

fun removeTaskThread(task: Task): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().send(TaskPayload(Member.emptyMember(), TaskOperation.REMOVE_TASK, task).toJson())
    })
}

fun changeTaskStatus(task: Task): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().send(TaskPayload(Member.emptyMember(), TaskOperation.CHANGE_TASK_STATUS, task).toJson())
    })
}


fun initializeConnectionWithTaskWSClient(): WSClient {
    return WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
}