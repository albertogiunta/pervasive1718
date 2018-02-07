package networking

import WSServer
import logic.Serializer
import logic.SessionController
import logic.SessionOperation
import logic.SessionPayload
import org.eclipse.jetty.websocket.api.Session

class WSSessionServer : WSServer<SessionPayload>() {

    init {
        SessionController.init(this)
    }

    private val sessionController = SessionController.INSTANCE

    override fun closed(session: Session, statusCode: Int, reason: String) {
        super.closed(session, statusCode, reason)
        sessionController.removeMember(session)
    }

    override fun message(session: Session, message: String) {
        super.message(session, message)

        val sessionPayload = Serializer.klaxon.parse<SessionPayload>(message)

        sessionPayload?.let {
            with(sessionPayload) {
                when (sessionOperation) {
                    SessionOperation.OPEN -> sessionController.createSession(leader, sessionId, session)
                    SessionOperation.ADD_MEMBER -> sessionController.addMember(leader, session)
                    SessionOperation.CLOSE -> sessionController.closeSession(sessionId)
                }
            }
        }
    }
}