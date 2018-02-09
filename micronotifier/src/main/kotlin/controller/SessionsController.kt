package controller

import logic.Member
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap

interface SessionsController<L, S> {

    fun openSession(sid: Long)

    fun setSessionFor(listener: L, session: S)

    fun getSessionOf(listener: L): S?

    fun removeListener(listener: L): S?

    fun removeListenerOn(session: S): Iterable<L>

    fun closeSession(sid: Long)

    companion object {
        const val DEFAULT_SESSION_VALUE: Long = -1L
    }
}

class NotifierSessionController private constructor() : SessionsController<Member, Session> {

    val sessionsMap = ConcurrentHashMap<Member, Session>()

    private var SID: Long = SessionsController.DEFAULT_SESSION_VALUE

    override fun openSession(sid: Long) {
        if (this.SID != SessionsController.DEFAULT_SESSION_VALUE) {
            //"http://localhost:8080/notifier/api/session/open/$sid".httpPost().responseString()
            this.SID = sid
        }
    }

    override fun setSessionFor(listener: Member, session: Session) {
        sessionsMap[listener] = session
    }

    override fun getSessionOf(listener: Member): Session? = sessionsMap[listener]

    override fun removeListener(listener: Member): Session? = sessionsMap.remove(listener)


    override fun removeListenerOn(session: Session): Iterable<Member> =
            sessionsMap.keySet(session).onEach { sessionsMap.remove(it) }


    override fun closeSession(sid: Long) {
        if (SID == sid) {
            //"http://localhost:8080/notifier/api/session/close/$sid".httpDelete().responseString()
            SID = -1
            sessionsMap.clear()
        }
    }

    companion object {

        private lateinit var controller: NotifierSessionController

        fun singleton() : NotifierSessionController = controller

    }
}