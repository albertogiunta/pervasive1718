@file:Suppress("UNUSED_PARAMETER")

package logic

enum class TaskOperation {
    ADD_LEADER,
    ADD_MEMBER,
    ADD_TASK,
    REMOVE_TASK,
    CHANGE_TASK_STATUS,
    ERROR_REMOVING_TASK,
    ERROR_CHANGING_STATUS
}

enum class Status(val id: Int) {
    RUNNING(2),
    SUSPENDED(1),
    MONITORING(5),
    FINISHED(3),
    ELIMINATED(4),
    EMPTY(6)
}