package model

import java.sql.Timestamp
import java.util.*
import KlaxonDate

data class Member(val id: Int, val name: String) {
    companion object {
        fun emptyMember(): Member = Member(EmptyMember.emptyMemberId, EmptyMember.emptyMemberName)

        fun defaultMember(): Member = Member(-52, "Member")
    }
}

object EmptyMember{
    const val emptyMemberId : Int = -2
    const val emptyMemberName: String = "empty member"
}

data class Session @JvmOverloads constructor(val id: Int = 0, val cf: String, @KlaxonDate val startDate: Timestamp = Timestamp(Date().time), @KlaxonDate val endDate: Timestamp? = null, val microServiceInstanceId: Int = 0)

data class Activity(val id: Int = 0, val name: String, val activityTypeId: Int, val acronym: String, val boundaryId: Int)

data class ActivityType(val id: Int = 0, val name: String) // farmaci, manovre, diagnostiche

data class HealthParameter(val id: Int = 0, val name: String, val acronym: String)

data class Log @JvmOverloads constructor(val id: Int = 0, val name: String, @KlaxonDate val logTime: Timestamp = Timestamp(Date().time), val healthParameterId: Int, val healthParameterValue: Double)

data class Operator(val id: Int = 0, val name: String, val surname: String, val roleId: Int, val isActive: Boolean)

data class Role(val id: Int = 0, val name: String) // leader, collaboratore, anestesista

data class Boundary(val id: Int = 0, val healthParameterId: Int, val activityId: Int, val upperBound: Double, val lowerBound: Double)

data class Task @JvmOverloads constructor(val id: Int = 0, val operatorId: Int, @KlaxonDate val startTime: Timestamp, @KlaxonDate val endTime: Timestamp, val activityId: Int, val statusId: Int)

data class TaskStatus(val id: Int = 0, val name: String) // sospseso, in corso, terminato, eliminato