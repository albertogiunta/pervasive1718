package model

data class ActionType(val id: Int, val name: String)

data class Activity(val id: Int, val name: String, val expectedEffect: String, val typeId: Int, val signature: String, val statusId: Int)

data class HealthParameter(val id: Int, val name: String, val signature: String)

data class Log(val id: Int, val name: String, val logTime: String, val healthParameterId: Int, val healthParameterValue: Double)

data class Operator(val id: Int, val name: String, val surname: String, val roleId: Int, val isActive: Boolean)

data class Role(val id: Int, val name: String)

data class Status(val id: Int, val healthParameterId: Int, val activityId: Int, val upperBound: Double, val lowerBound: Double)

data class Task(val id: Int, val operatorId: Int, val startTime: String, val endTime: String, val activityId: Int, val progress: String)