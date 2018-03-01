package utils

import WSClient
import config.Services
import model.*
import java.sql.Timestamp
import java.util.*

@Suppress("UNUSED_PARAMETER")
fun mockLeader(memberCF: String,
               leaderWS: WSClient,
               leader: Member = Member("Leader")) {

    val message = PayloadWrapper(Services.instanceId(), WSOperations.ADD_LEADER,
        MembersAdditionNotification(listOf(leader)).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(2000) }
}

fun mockLeaderAndMembers(memberCF: String,
                         leaderWS: WSClient,
                         memberWS: WSClient,
                         leader: Member = Member("Leader"),
                         member: Member = Member("Member")) {
    mockLeader(memberCF, leaderWS, leader).also { Thread.sleep(1000) }
    val member2 = Member("Member2")

    val message1 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_MEMBER,
        MembersAdditionNotification(listOf(member)).toJson())
    val message2 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_MEMBER,
        MembersAdditionNotification(listOf(member2)).toJson())

    memberWS.sendMessage(message1.toJson()).also { Thread.sleep(1000) }
    memberWS.sendMessage(message2.toJson()).also { Thread.sleep(1000) }
}

fun mockLeaderMemberInteractionAndTaskAddition(session: SessionDNS,
                                               memberCF: String,
                                               taskID: Int,
                                               leaderWS: WSClient,
                                               memberWS: WSClient,
                                               leader: Member = Member("Leader"),
                                               member: Member = Member("Member"),
                                               augmentedTask: AugmentedTask = AugmentedTask(
                                                       Task(
                                                               taskID,
                                                               session.sessionId,
                                                               member.userCF,
                                                               Timestamp(Date().time),
                                                               Timestamp(Date().time + 1000),
                                                               1,
                                                               Status.RUNNING.id),
                                                       listOf(
                                                               LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
                                                               LifeParameters.SYSTOLIC_BLOOD_PRESSURE,
                                                               LifeParameters.HEART_RATE),
                                                       "nome attività")) {

    mockLeader(memberCF, leaderWS, leader).also { Thread.sleep(1000) }

    val message1 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_MEMBER,
        MembersAdditionNotification(listOf(member)).toJson())
    val message2 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_TASK,
        TaskAssignment(member, augmentedTask).toJson())

    memberWS.sendMessage(message1.toJson()).also { Thread.sleep(1000) }
    leaderWS.sendMessage(message2.toJson()).also { Thread.sleep(1000) }
    Thread.sleep(5000)
}

fun mockLeaderMemberInteractionAndTaskRemoval(session: SessionDNS,
                                              memberCF: String,
                                              taskID: Int,
                                              leaderWS: WSClient,
                                              memberWS: WSClient,
                                              member: Member = Member("Member"),
                                              augmentedTask: AugmentedTask = AugmentedTask(
                                                      Task(
                                                              taskID,
                                                              session.sessionId,
                                                              member.userCF,
                                                              Timestamp(Date().time),
                                                              Timestamp(Date().time + 1000),
                                                              1,
                                                              Status.RUNNING.id),
                                                      listOf(
                                                              LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
                                                              LifeParameters.SYSTOLIC_BLOOD_PRESSURE,
                                                              LifeParameters.HEART_RATE),
                                                      "nome attività")) {

    mockLeaderMemberInteractionAndTaskAddition(session, memberCF, taskID, leaderWS, memberWS)

    val message = PayloadWrapper(Services.instanceId(), WSOperations.REMOVE_TASK,
        TaskAssignment(member, augmentedTask).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(1000) }
}

fun mockLeaderMemberInteractionAndTaskChange(session: SessionDNS,
                                             memberCF: String,
                                             taskID: Int,
                                             leaderWS: WSClient,
                                             memberWS: WSClient,
                                             member: Member = Member("Member"),
                                             augmentedTask: AugmentedTask = AugmentedTask(
                                                     Task(
                                                             taskID,
                                                             session.sessionId,
                                                             member.userCF,
                                                             Timestamp(Date().time),
                                                             Timestamp(Date().time + 1000),
                                                             1,
                                                             Status.RUNNING.id),
                                                     listOf(
                                                             LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
                                                             LifeParameters.SYSTOLIC_BLOOD_PRESSURE,
                                                             LifeParameters.HEART_RATE),
                                                     "nome attività")) {

    mockLeaderMemberInteractionAndTaskAddition(session, memberCF, taskID, leaderWS, memberWS)
    augmentedTask.task.statusId = Status.FINISHED.id

    val message = PayloadWrapper(Services.instanceId(), WSOperations.CHANGE_TASK_STATUS,
        TaskAssignment(member, augmentedTask).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(1000) }
}

