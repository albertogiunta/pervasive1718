package microdb.model

data class Activity(val id: Int, val name: String, val activityTypeId: Int, val acronym: String, val boundaryId: Int)

data class ActivityType(val id: Int, val name: String) // farmaci, manovre, diagnostiche

data class HealthParameter(val id: Int, val name: String, val acronym: String)

data class Log(val id: Int, val name: String, val logTime: String, val healthParameterId: Int, val healthParameterValue: Double)

data class Operator(val id: Int, val name: String, val surname: String, val roleId: Int, val isActive: Boolean)

data class Role(val id: Int, val name: String) // leader, collaboratore, anestesista

data class Boundary(val id: Int, val healthParameterId: Int, val activityId: Int, val upperBound: Double, val lowerBound: Double)

data class Task(val id: Int, val operatorId: Int, val startTime: String, val endTime: String, val activityId: Int, val taskStatusId: String)

data class TaskStatus(val id: Int, val name: String) // sospseso, in corso, terminato, eliminato