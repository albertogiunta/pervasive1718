package networking.ws

import WSServer
import com.google.gson.GsonBuilder
import controller.SessionsController
import logic.Member
import logic.SessionOperation
import model.Join
import model.Payload
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket

@WebSocket
class SessionService(val sessionController: SessionsController<Member, Session>) :
        WSServer<Payload<SessionOperation, Member>>() {

    val Gson = GsonBuilder().create()

    override fun closed(session: Session, statusCode: Int, reason: String) {
        super.closed(session, statusCode, reason)
        sessionController.removeListenerOn(session)
    }

    override fun message(session: Session, message: String) {
        super.message(session, message)

        val request = Gson.fromJson(message, Join::class.java)

        with(request) {
            when (topic) {
                SessionOperation.OPEN -> {
                    sessionController.openSession(sid)
                    sessionController.setSessionFor(body, session)
                }
                SessionOperation.ADD_MEMBER -> sessionController.setSessionFor(body, session)
                SessionOperation.CLOSE -> sessionController.closeSession(sid)
            }
        }
    }

}