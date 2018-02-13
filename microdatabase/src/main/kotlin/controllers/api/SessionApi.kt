@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import KlaxonDate
import Params
import badRequest
import com.beust.klaxon.Klaxon
import controllers.SubscriberController
import dao.SessionDao
import dateConverter
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
        val session: Session = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Session>(request.body())
                ?: return response.badRequest()

        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java) {
            it.insertNewSession(session.id, session.patId, session.date)
        }

        SubscriberController.startListeningMonitorsForSession(session.id)

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
     * Deletes a session based on the session ID
     */
    fun removeSessionBySessionId(request: Request, response: Response): String {
        val sessionId = request.params(Params.Session.SESSION_ID).toInt()

        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java) {
            it.deleteSessionBySessionId(sessionId)
        }

        SubscriberController.stopListeningMonitorsForSession(sessionId)

        return response.ok()
    }
}