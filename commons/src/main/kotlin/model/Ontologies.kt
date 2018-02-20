@file:Suppress("UNUSED_PARAMETER")

package model

enum class Status(val id: Int) {
    RUNNING(2),
    SUSPENDED(1),
    MONITORING(5),
    FINISHED(3),
    ELIMINATED(4),
    EMPTY(6)
}