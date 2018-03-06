@file:Suppress("UNUSED_PARAMETER")

package model

enum class Status(val id: Int) {
    SUSPENDED(1),
    RUNNING(2),
    FINISHED(3),
    ELIMINATED(4),
    MONITORING(5),
    EMPTY(6)
}