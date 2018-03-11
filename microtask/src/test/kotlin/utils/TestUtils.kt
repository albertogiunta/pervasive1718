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

    val message = PayloadWrapper(Services.instanceId(), WSOperations.ADD_LEADER, leader.toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(2000) }
}

fun mockLeaderAndMembers(memberCF: String,
                         leaderWS: WSClient,
                         memberWS: WSClient,
                         leader: Member = Member("Leader"),
                         member: Member = Member("Member")) {
    mockLeader(memberCF, leaderWS, leader).also { Thread.sleep(1000) }
    val member2 = Member("Member2")

    val message1 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_MEMBER, member.toJson())
    val message2 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_MEMBER, member2.toJson())

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
                                                       Task.newTask(session.sessionId, 1, memberCF),
                                                       listOf(
                                                               LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
                                                               LifeParameters.SYSTOLIC_BLOOD_PRESSURE,
                                                               LifeParameters.HEART_RATE),
                                                       "nome attività")): AugmentedTask {

    mockLeader(memberCF, leaderWS, leader).also { Thread.sleep(1000) }

    val message1 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_MEMBER, member.toJson())

    val message2 = PayloadWrapper(Services.instanceId(), WSOperations.ADD_TASK,
        TaskAssignment(member, augmentedTask).toJson())

    memberWS.sendMessage(message1.toJson()).also { Thread.sleep(1000) }
    leaderWS.sendMessage(message2.toJson()).also { Thread.sleep(1000) }
    Thread.sleep(5000)

    return augmentedTask
}

fun mockLeaderMemberInteractionAndTaskRemoval(session: SessionDNS,
                                              memberCF: String,
                                              taskID: Int,
                                              leaderWS: WSClient,
                                              memberWS: WSClient,
                                              member: Member = Member("Member"),
                                              augmentedTask: AugmentedTask = AugmentedTask(
                                                      Task.newTask(session.sessionId, 1, memberCF),
                                                      listOf(
                                                              LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
                                                              LifeParameters.SYSTOLIC_BLOOD_PRESSURE,
                                                              LifeParameters.HEART_RATE),
                                                      "nome attività")): AugmentedTask {

    mockLeaderMemberInteractionAndTaskAddition(session, memberCF, taskID, leaderWS, memberWS, augmentedTask = augmentedTask)

    val message = PayloadWrapper(Services.instanceId(), WSOperations.REMOVE_TASK, TaskAssignment(member, augmentedTask).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(1000) }

    return augmentedTask
}

fun mockLeaderMemberInteractionAndTaskChange(session: SessionDNS,
                                             memberCF: String,
                                             taskID: Int,
                                             leaderWS: WSClient,
                                             memberWS: WSClient,
                                             member: Member = Member("Member"),
                                             augmentedTask: AugmentedTask = AugmentedTask(
                                                     Task.newTask(session.sessionId, 1, memberCF, end = Timestamp(Date().time + 2000)),
                                                     listOf(
                                                             LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
                                                             LifeParameters.SYSTOLIC_BLOOD_PRESSURE,
                                                             LifeParameters.HEART_RATE),
                                                     "nome attività")): AugmentedTask {

    mockLeaderMemberInteractionAndTaskAddition(session, memberCF, taskID, leaderWS, memberWS, augmentedTask = augmentedTask)
    augmentedTask.task.statusId = Status.FINISHED.id

    println(augmentedTask)

    val message = PayloadWrapper(Services.instanceId(), WSOperations.CHANGE_TASK_STATUS,
        TaskAssignment(member, augmentedTask).toJson())

    leaderWS.sendMessage(message.toJson()).also { Thread.sleep(1000) }

    return augmentedTask
}

