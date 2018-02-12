package controller

import logic.Member
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap

/**
 * A controller interface that manage generic sessions for multiple user
 *
 * @param L user type
 * @param S session type
 * (ex: websocket,...)
 * */
interface SessionsController<L, S> {

    /**
     * Set a new session for the specified listener
     * */
    operator fun set(listener: L, session: S)

    /**
     * Get the listener's session
     * */
    operator fun get(listener: L): S?

    /**
     * Remove the listener's session and return it if present
     * */
    fun removeListener(listener: L): S?

    /**
     * Remove all the listener on a specified session and return them
     * */
    fun removeListenerOn(session: S): Iterable<L>

    /**
     * Remove all members and respective session
     * */
    fun close()
}

class NotifierSessionsController private constructor() : SessionsController<Member, Session> {

    private val sessionsMap = ConcurrentHashMap<Member, Session>()


    override fun get(listener: Member): Session? = sessionsMap[listener]


    override operator fun set(listener: Member, session: Session) {
        sessionsMap[listener] = session
    }

    override fun removeListener(listener: Member): Session? = sessionsMap.remove(listener)


    override fun removeListenerOn(session: Session): Iterable<Member> =
            sessionsMap.filter { it.value.equals(session) }.keys.toList().onEach { sessionsMap.remove(it) }


    override fun close() {
        sessionsMap.clear()
    }

    companion object {

        private var controller: NotifierSessionsController = NotifierSessionsController()

        fun singleton(): NotifierSessionsController = controller

    }
}