package controllers

import java.util.concurrent.atomic.AtomicBoolean

object SessionController {

    private var sessionId: Int = -1
    private val isSet: AtomicBoolean = AtomicBoolean(false)

    fun attachSession(id: Int): Boolean {
        if (!isSet.getAndSet(true)) {
            sessionId = id
            return true
        }
        return false
    }

    fun detachSession() {
        sessionId = -1
        isSet.set(false)
    }

    fun getCurrentSession() = sessionId
}