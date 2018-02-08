package logic

import networking.WSSessionServer
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class SessionController private constructor(private val ws: WSSessionServer) {

    lateinit var leader: Pair<Member, Session>
    val members: ConcurrentHashMap<Member, Session> = ConcurrentHashMap()

    private var sessionId: Int = -1

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
        sessionId = traumaSessionId
    }

    fun closeSession(traumaSessionId: Int) {
        // TODO comunica al db
        removeAllMembers()
    }

    fun addMember(member: Member, session: Session) {
        members[member] = session
        // TODO comunica al member che è arrivato un nuovo member
        ws.sendMessage(leader.second, SessionPayload(member, SessionOperation.ADD_MEMBER, sessionId))
    }

    fun removeMember(session: Session) {
        members.keySet(session).forEach { members.remove(it) }
    }

    private fun removeAllMembers() {
        members.clear()
    }
}