package model

import java.sql.Timestamp
import java.util.*

data class Activity(val id: Int = 0, val name: String, val activityTypeId: Int, val acronym: String, val boundaryId: Int)

data class ActivityType(val id: Int = 0, val name: String) // farmaci, manovre, diagnostiche

data class HealthParameter(val id: Int = 0, val name: String, val acronym: String)

data class Log(val id: Int = 0, val name: String, val logTime: Timestamp = Timestamp(Date().time), val healthParameterId: Int, val healthParameterValue: Double)

data class Operator(val id: Int = 0, val name: String, val surname: String, val roleId: Int, val isActive: Boolean)

data class Role(val id: Int = 0, val name: String) // leader, collaboratore, anestesista

data class Boundary(val id: Int = 0, val healthParameterId: Int, val activityId: Int, val upperBound: Double, val lowerBound: Double)

data class Task(val id: Int = 0, val operatorId: Int, val startTime: Timestamp, val endTime: Timestamp, val activityId: Int, val taskStatusId: String)

data class TaskStatus(val id: Int = 0, val name: String) // sospseso, in corso, terminato, eliminato