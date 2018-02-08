@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.SessionDao
import model.Session
import ok
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object SessionApi {

    /**
     * Retrieves all the session
     */
    fun addSession(request: Request, response: Response): String {
        val session: Session = Klaxon().parse<Session>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java) {
            it.insertNewSession(session.sessionId, session.roomId)
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the sessions
     */
    fun getAllSessions(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Session>, SessionDao, SQLException>(SessionDao::class.java)
        { it.selectAllSessions() }
            .toJson()
    }

    /**
     * Retrieves a session base on the session ID
     */
    fun getSessionBySessionId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Session>, SessionDao, SQLException>(SessionDao::class.java)
        { it.selectSessionBySessionId(request.params(Params.Session.SESSION_ID).toInt()) }
            .toJson()
    }

    /**
     * Retrieves a session base on the room ID
     */
    fun getSessionByRoomId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Session>, SessionDao, SQLException>(SessionDao::class.java)
        { it.selectSessionByRoomId(request.params(Params.Session.ROOM_ID).toInt()) }
            .toJson()
    }


    /**
     * Deletes a session based on the session ID
     */
    fun removeSessionBySessionId(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java) {
            it.deleteSessionBySessionId(request.params(Params.Session.SESSION_ID).toInt())
        }
        return response.ok()
    }

    /**
     * Deletes a session based on the room ID
     */
    fun removeSessionByRoomId(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java) {
            it.deleteSessionByRoomId(request.params(Params.Session.ROOM_ID).toInt())
        }
        return response.ok()
    }
}