package controller

import model.Member
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap

/**
 * A controller interface that manage generic sessions for multiple user
 *
 * @param L user type
 * @param S session type
 * (ex: websocket,...)
 * */
interface SessionsManager<L, S> {

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

class NotifierSessionsManager : SessionsManager<Member, Session> {

    private val sessionsMap = ConcurrentHashMap<Member, Session>()

    override fun get(listener: Member): Session? = sessionsMap[listener]

    override operator fun set(listener: Member, session: Session) {
        sessionsMap[listener] = session
    }

    override fun getOn(session: Session): Member? =
        sessionsMap.filter{ it.value == session }.keys.first()

    override fun removeListener(listener: Member): Session? = sessionsMap.remove(listener)

    override fun removeListenerOn(session: Session): Iterable<Member> =
            sessionsMap.filter { it.value == session }.keys.toList().onEach { sessionsMap.remove(it) }

    override fun contains(listener: Member): Boolean = sessionsMap.containsKey(listener)

    override fun has(session: Session): Boolean = sessionsMap.containsValue(session)

    override fun close() = sessionsMap.clear()

}