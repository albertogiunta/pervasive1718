package model

import utils.KlaxonDate
import utils.KlaxonLifeParameterList
import java.sql.Timestamp
import java.util.*

data class Session @JvmOverloads constructor(val id: Int = 0, val patientCF: String, val leaderCF: String, @KlaxonDate var startDate: Timestamp = Timestamp(Date().time), @KlaxonDate var endDate: Timestamp? = null, val microServiceInstanceId: Int = 0) {
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

data class VisibleTask(val taskName: String, val name: String, val operatorCF: String, val operatorName: String, val operatorSurname: String)

data class Member(val userCF: String) {
    companion object {
        fun emptyMember(): Member = Member(EmptyMember.emptyMemberName)

        fun defaultMember(): Member = Member("Member")
    }
}

data class Task @JvmOverloads constructor(var id: Int = 0, val name: String, val sessionId: Int, val operatorCF: String, @KlaxonDate val startTime: Timestamp, @KlaxonDate var endTime: Timestamp? = null, val activityId: Int, var statusId: Int) {

    companion object {
        fun emptyTask(): Task =
                Task(EmptyTask.emptyTaskId, EmptyTask.emptyTaskName, EmptyTask.emptySessionId, EmptyTask.emptyTaskOperatorId, EmptyTask.emptyTaskStartTime, EmptyTask.emptyTaskEndTime, EmptyTask.emptyTaskActivityId, EmptyTask.emptyTaskStatusId)

        fun defaultTask(): Task =
                Task(1, "Task", -1, "CF", Timestamp(Date().time), Timestamp(Date().time + 1000), 1, Status.RUNNING.id)
    }
}

data class TaskReportEntry @JvmOverloads constructor(
        val sessionId: Int,
        val taskStrId: String,
        val leaderCF: String,
        val patientCF: String,
        val activityAcronym: String,
        val activityName: String,
        @KlaxonLifeParameterList val relatedHealthParameters: List<LifeParameters>,
        @KlaxonDate val startTime: Timestamp? = null,
        @KlaxonDate val endTime: Timestamp? = null,
        val operatorCF: String? = null)

data class LogReportEntry @JvmOverloads constructor(
        val sessionId: Int,
        val leaderCF: String,
        val patientCF: String,
        @KlaxonDate val dateTime: Timestamp?,
        val healthParameter: String,
        val hpValue: Double)

data class AugmentedMemberFromServer(val userCF: String, val items: MutableList<AugmentedTask> = mutableListOf())

data class AugmentedTask(val task: Task, val linkedParameters: List<LifeParameters>, val activityName: String)