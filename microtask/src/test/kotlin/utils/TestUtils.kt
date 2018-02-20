package utils

import WSClient
import config.Services
import model.*
import java.sql.Timestamp
import java.util.*

fun mockLeader(memberId: Int,
               leaderWS: WSClient,
               leader: Member = Member(memberId, "Leader")){

    val message = PayloadWrapper(Services.instanceId().toLong(), WSOperations.ADD_LEADER,
            MembersAdditionNotification(listOf(leader)).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(2000) }
}

fun mockLeaderAndMembers(memberId: Int,
                            leaderWS: WSClient,
                            memberWS: WSClient,
                            leader: Member = Member(memberId, "Leader"),
                            member: Member = Member(memberId, "Member")){
    mockLeader(memberId,leaderWS,leader).also { Thread.sleep(1000) }
    val member2:Member = Member(memberId+1,"Member2")

    val message1 = PayloadWrapper(Services.instanceId().toLong(), WSOperations.ADD_MEMBER,
            MembersAdditionNotification(listOf(member)).toJson())
    val message2 = PayloadWrapper(Services.instanceId().toLong(), WSOperations.ADD_MEMBER,
            MembersAdditionNotification(listOf(member2)).toJson())

    memberWS.sendMessage(message1.toJson()).also { Thread.sleep(1000) }
    memberWS.sendMessage(message2.toJson()).also { Thread.sleep(1000) }
}

fun mockLeaderMemberInteractionAndTaskAddition(session: SessionDNS,
                                               memberId: Int,
                                               taskID: Int,
                                               leaderWS: WSClient,
                                               memberWS: WSClient,
                                               leader: Member = Member(memberId, "Leader"),
                                               member: Member = Member(memberId, "Member"),
                                               task: Task = Task(taskID, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)) {

    mockLeader(memberId,leaderWS,leader).also { Thread.sleep(1000) }

    val message1 = PayloadWrapper(Services.instanceId().toLong(), WSOperations.ADD_MEMBER,
            MembersAdditionNotification(listOf(member)).toJson())
    val message2 = PayloadWrapper(Services.instanceId().toLong(), WSOperations.ADD_TASK,
            TaskAssignment(member, task).toJson())

    memberWS.sendMessage(message1.toJson()).also { Thread.sleep(1000) }
    leaderWS.sendMessage(message2.toJson()).also { Thread.sleep(1000) }
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

    val message = PayloadWrapper(Services.instanceId().toLong(), WSOperations.REMOVE_TASK,
            TaskAssignment(member, task).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(1000) }
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

    val message = PayloadWrapper(Services.instanceId().toLong(), WSOperations.CHANGE_TASK_STATUS,
            TaskAssignment(member, task).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(1000) }
}

