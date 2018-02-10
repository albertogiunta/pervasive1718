package controller

import logic.Member
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap

interface SessionsController<L, S> {

    fun open(sid: Long)

    operator fun set(listener: L, session: S)

    operator fun get(listener: L): S?

    fun removeListener(listener: L): S?

    fun removeListenerOn(session: S): Iterable<L>

    fun closeSession(sid: Long)

    companion object {
        const val DEFAULT_SESSION_VALUE: Long = -1L
    }
}

class NotifierSessionsController private constructor() : SessionsController<Member, Session> {

    private val sessionsMap = ConcurrentHashMap<Member, Session>()

    private var SID: Long = SessionsController.DEFAULT_SESSION_VALUE

    override fun open(sid: Long) {
        if (this.SID != SessionsController.DEFAULT_SESSION_VALUE) {
            this.SID = sid
        }
    }

    override fun get(listener: Member): Session? = sessionsMap[listener]


    override operator fun set(listener: Member, session: Session) {
        sessionsMap[listener] = session
    }

    override fun removeListener(listener: Member): Session? = sessionsMap.remove(listener)


    override fun removeListenerOn(session: Session): Iterable<Member> =
            sessionsMap.keySet(session).onEach { sessionsMap.remove(it) }

    override fun closeSession(sid: Long) {
        if (SID == sid) {
            SID = -1
            sessionsMap.clear()
        }
    }

    companion object {

        private var controller: NotifierSessionsController = NotifierSessionsController()

        fun singleton(): NotifierSessionsController = controller

    }
}