package logic

enum class TaskOperation { ADD_TASK, REMOVE_TASK, CHANGE_TASK_STATUS }

enum class SessionOperation { OPEN, CLOSE, ADD_MEMBER }

enum class Status { RUNNING, SUSPENDED, MONITORING, FINISHED, ELIMINATED, EMPTY }