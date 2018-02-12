package model

data class VisibleTask(val id: Int, val name: String, val priority: Priority, val operatorName: String, val operatorSurname: String)

enum class Priority { HIGH, LOW }