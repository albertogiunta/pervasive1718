package networking

import WSServer
import logic.Serializer
import logic.SessionController
import logic.SessionOperation
import logic.SessionPayload
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket

@WebSocket
class WSSessionServer : WSServer<SessionPayload>() {

    init {
        SessionController.init(this)
    }

    private val sessionController = SessionController.INSTANCE

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        super.onClose(session, statusCode, reason)
        sessionController.removeMember(session)
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)

        val sessionPayload = Serializer.klaxon.parse<SessionPayload>(message)

        sessionPayload?.let {
            with(sessionPayload) {
                when (sessionOperation) {
                    SessionOperation.OPEN -> sessionController.createSession(member, sessionId, session)
                    SessionOperation.ADD_MEMBER -> sessionController.addMember(member, session)
                    SessionOperation.CLOSE -> sessionController.closeSession(sessionId)
                }
            }
        }
    }
}