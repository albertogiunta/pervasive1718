@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import controllers.SubscriberController
import dao.SessionDao
import model.Activity
import model.Session
import ok
import spark.Request
import spark.Response
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

object SessionApi {

    /**
     * Adds a session
     */
    fun addSession(request: Request, response: Response): String {
        var session: Session = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Session>(request.body())
                ?: return response.badRequest()
        session.startDate = Timestamp(Date().time)
        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java)
        {
            session =
                when { session.id == -1 -> it.insertNewSession(session.patientCF,
                        session.leaderCF,
                        session.startDate,
                        session.microServiceInstanceId)
                    else -> it.insertNewSessionWithId(session.id,
                        session.patientCF,
                        session.leaderCF,
                        session.startDate,
                        session.microServiceInstanceId)
            }
        }

        SubscriberController.startListeningMonitorsForInstanceId(session)

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
     * Retrieves all the open sessions by leader id
     */
    fun getAllOpenSessionsByLeaderCF(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Session>, SessionDao, SQLException>(SessionDao::class.java)
        { it.selectAllOpenSessionsByLeaderCF(request.params(Params.Session.LEADER_CF)) }
            .toJson()
    }

    /**
     * Deletes a session based on the session ID
     */
    fun closeSessionBySessionId(request: Request, response: Response): String {
        val sessionId = request.params(Params.Session.SESSION_ID).toInt()

        SubscriberController.stopListeningMonitorsForSession()

        JdbiConfiguration.INSTANCE.jdbi.useExtension<SessionDao, SQLException>(SessionDao::class.java)
        { it.closeSessionBySessionId(sessionId, Timestamp(Date().time)) }

        return response.ok()
    }
}