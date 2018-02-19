package controllers

import java.util.concurrent.atomic.AtomicBoolean

object InstanceIdController {

    private var sessionId: Int = -1
    private val isSet: AtomicBoolean = AtomicBoolean(false)

    fun attachInstanceId(id: Int): Boolean {
        if (!isSet.getAndSet(true)) {
            sessionId = id
            return true
        }
        return false
    }

    fun detachInstance() {
//        sessionId = -1
        isSet.set(false)
    }

    fun isAttached(): Boolean {
        return isSet.get()
    }

    fun getCurrentInstanceID() = sessionId
}