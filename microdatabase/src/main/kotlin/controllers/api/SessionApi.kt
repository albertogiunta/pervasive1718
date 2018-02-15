@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import controllers.SubscriberController
import dao.SessionDao
import model.Session
import ok
import spark.Request
import spark.Response
import utils.toJson
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

object SessionApi {

    /**
     * Retrieves all the session
     */
    fun addSession(request: Request, response: Response): String {
        var session = Session(cf = "", startDate = Timestamp(Date().time))
        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java)
        { session = it.insertNewSession(request.params(Params.Session.PAT_ID), Timestamp(Date().time), request.params(Params.Session.INSTANCE_ID).toInt()) }

        SubscriberController.startListeningMonitorsForSession(session.id)

        return session.toJson()
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
     * Retrieves all the open sessions
     */
    fun getAllOpenSessions(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Session>, SessionDao, SQLException>(SessionDao::class.java)
        { it.selectAllOpenSessions() }
            .toJson()
    }


    /**
     * Deletes a session based on the session ID
     */
    fun closeSessionBySessionId(request: Request, response: Response): String {
        val sessionId = request.params(Params.Session.SESSION_ID).toInt()

        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java)
        { it.closeSessionBySessionId(sessionId, Timestamp(Date().time)) }

        SubscriberController.stopListeningMonitorsForSession(sessionId)

        return response.ok()
    }
}