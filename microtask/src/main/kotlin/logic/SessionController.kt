package logic

import networking.WSSessionServer
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class SessionController private constructor(private val ws: WSSessionServer) {

    val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()

    lateinit var leader: Pair<Member, Session>

    companion object {
        lateinit var INSTANCE: SessionController
        private val isInitialized = AtomicBoolean()

        fun init(ws: WSSessionServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = SessionController(ws)
            }
        }
    }

    fun createSession(member: Member, traumaSessionId: Int, session: Session) {
        // TODO metti traumaSessionId nel db
        leader = Pair(member, session)
    }

    fun closeSession(traumaSessionId: Int) {
        // TODO comunica al db
        removeAllMembers()
    }

    fun addMember(member: Member, session: Session) {
        members[member] = session
        // TODO comunica al leader che è arrivato un nuovo member
    }

    fun removeMember(session: Session) {
        members.keySet(session).forEach { members.remove(it) }
    }

    private fun removeAllMembers() {

    }
}