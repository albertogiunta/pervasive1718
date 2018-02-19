package controllers

import model.Session
import java.util.concurrent.atomic.AtomicBoolean

object SessionController {

    private var session: Session = Session.emptySession()

    private val isSet: AtomicBoolean = AtomicBoolean(false)

    fun attachInstanceId(newSession: Session): Boolean {
        if (!isSet.getAndSet(true)) {
            session = newSession
            return true
        }
        return false
    }

    fun detachInstance() = isSet.set(false)

    fun isAttached(): Boolean = isSet.get()

    fun getCurrentInstanceId() = session.microServiceInstanceId

    fun getCurrentSessionId() = session.id
}