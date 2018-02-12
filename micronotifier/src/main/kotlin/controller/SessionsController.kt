package controller

import logic.Member
import org.eclipse.jetty.websocket.api.Session

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
     * Get the listener associated to the given session
     */
    fun getOn(session: S) : L?

    /**
     * Remove the listener's session and return it if present
     * */
    fun removeListener(listener: L): S?

    /**
     * Remove all the listener on a specified session and return them
     * */
    fun removeListenerOn(session: S): Iterable<L>

    fun contains(listener: L) : Boolean

    fun has(session: S) : Boolean
    /**
     * Remove all members and respective session
     * */
    fun close()
}

class NotifierSessionsController private constructor() : SessionsController<Member, Session> {

    val sessionsMap = mutableMapOf<Member, Session>()

    @Synchronized
    override fun get(listener: Member): Session? = sessionsMap[listener]

    @Synchronized
    override operator fun set(listener: Member, session: Session) {
        sessionsMap.put(listener, session)
    }

    @Synchronized
    override fun getOn(session: Session): Member? =
        sessionsMap.filter{ it.value == session }.keys.first()

    @Synchronized
    override fun removeListener(listener: Member): Session? = sessionsMap.remove(listener)

    @Synchronized
    override fun removeListenerOn(session: Session): Iterable<Member> =
            sessionsMap.filter { it.value == session }.keys.toList().onEach { sessionsMap.remove(it) }

    @Synchronized
    override fun contains(listener: Member): Boolean = sessionsMap.containsKey(listener)

    override fun has(session: Session): Boolean = sessionsMap.containsValue(session)

    @Synchronized
    override fun close() {
        sessionsMap.clear()
    }

    companion object {

        private var controller: NotifierSessionsController = NotifierSessionsController()

        fun singleton(): NotifierSessionsController = controller

    }
}