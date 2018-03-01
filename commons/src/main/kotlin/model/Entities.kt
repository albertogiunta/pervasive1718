package model

import utils.KlaxonDate
import java.sql.Timestamp
import java.util.*

data class Session @JvmOverloads constructor(val id: Int = 0, val patientCF: String, val leaderCF: String, @KlaxonDate val startDate: Timestamp = Timestamp(Date().time), @KlaxonDate val endDate: Timestamp? = null, val microServiceInstanceId: Int = 0) {
    companion object {
        fun emptySession(): Session = Session(-1, "-1", leaderCF = "cf a caso", microServiceInstanceId = -1)
    }
}

data class Activity(val id: Int = 0, val name: String, val activityTypeId: Int, val acronym: String, val healthParameterIds: List<Int>)

data class ActivityType(val id: Int = 0, val name: String) // farmaci, manovre, diagnostiche

data class HealthParameter(val id: Int = 0, val name: String, val acronym: String)

data class Log @JvmOverloads constructor(val id: Int = 0, val name: String, @KlaxonDate val logTime: Timestamp = Timestamp(Date().time), val healthParameterId: Int, val healthParameterValue: Double)

data class Operator(val id: Int = 0, val operatorCF: String, val name: String, val surname: String, val roleId: Int, val isActive: Boolean)

data class Role(val id: Int = 0, val name: String) // leader, collaboratore, anestesista

data class Boundary(val id: Int = 0, val healthParameterId: Int, val upperBound: Double, val lowerBound: Double, val lightWarningOffset: Double, val status: String, val itsGood: Boolean, val minAge: Double, val maxAge: Double)

data class TaskStatus(val id: Int = 0, val name: String) // sospseso, in corso, terminato, eliminato

data class VisibleTask(val id: Int, val name: String, val operatorCF: String, val operatorName: String, val operatorSurname: String)

data class Member(val userCF: String) {
    companion object {
        fun emptyMember(): Member = Member(EmptyMember.emptyMemberName)

        fun defaultMember(): Member = Member("Member")
    }
}

data class Task @JvmOverloads constructor(val id: Int = 0, val sessionId: Int, val operatorCF: String, @KlaxonDate val startTime: Timestamp, @KlaxonDate val endTime: Timestamp, val activityId: Int, var statusId: Int) {
    companion object {
        fun emptyTask(): Task =
                Task(EmptyTask.emptyTaskId, EmptyTask.emptySessionId, EmptyTask.emptyTaskOperatorId, EmptyTask.emptyTaskStartTime, EmptyTask.emptyTaskEndTime, EmptyTask.emptyTaskActivityId, EmptyTask.emptyTaskStatusId)

        fun defaultTask(): Task =
            Task(1, -1, "CF a casissimo", Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)
    }
}

data class AugmentedTask(val task: Task, val linkedParameters: List<LifeParameters>)