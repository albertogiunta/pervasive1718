package utils

import URIFactory
import WSClient
import WSClientInitializer
import model.*
import java.sql.Timestamp
import java.util.*

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

fun mockLeaderMemberInteractionAndTaskAddition(session: SessionDNS,
                                               memberId: Int,
                                               taskID: Int,
                                               leaderWS: WSClient,
                                               memberWS: WSClient,
                                               leader: Member = Member(memberId, "Leader"),
                                               member: Member = Member(memberId, "Member"),
                                               task: Task = Task(taskID, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)) {


    leaderWS.sendMessage(TaskPayload(leader, TaskOperation.ADD_LEADER, Task.emptyTask()).toJson()).also { Thread.sleep(1000) }
    memberWS.sendMessage(TaskPayload(member, TaskOperation.ADD_MEMBER, Task.emptyTask()).toJson()).also { Thread.sleep(1000) }
    leaderWS.sendMessage(TaskPayload(member, TaskOperation.ADD_TASK, task).toJson()).also { Thread.sleep(1000) }
    Thread.sleep(5000)
}

fun mockLeaderMemberInteractionAndTaskRemoval(session: SessionDNS,
                                              memberId: Int,
                                              taskID: Int,
                                              leaderWS: WSClient,
                                              memberWS: WSClient,
                                              member: Member = Member(memberId, "Member"),
                                              task: Task = Task(taskID, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)) {

    mockLeaderMemberInteractionAndTaskAddition(session, memberId, taskID, leaderWS, memberWS)
    leaderWS.sendMessage(TaskPayload(member, TaskOperation.REMOVE_TASK, task).toJson()).also { Thread.sleep(1000) }
}

fun mockLeaderMemberInteractionAndTaskChange(session: SessionDNS,
                                             memberId: Int,
                                             taskID: Int,
                                             leaderWS: WSClient,
                                             memberWS: WSClient,
                                             member: Member = Member(memberId, "Member"),
                                             task: Task = Task(taskID, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)) {

    mockLeaderMemberInteractionAndTaskAddition(session, memberId, taskID, leaderWS, memberWS)
    task.statusId = Status.FINISHED.id
    leaderWS.sendMessage(TaskPayload(member, TaskOperation.CHANGE_TASK_STATUS, task).toJson()).also { Thread.sleep(1000) }
}

