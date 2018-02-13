package dao

import Params.Session.DATE
import Params.Session.PAT_ID
import Params.Session.SESSION_ID
import Params.Session.TABLE_NAME
import model.Session
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Timestamp

interface SessionDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($SESSION_ID, $PAT_ID, $DATE) VALUES (:$SESSION_ID, :$PAT_ID, :$DATE)")
    fun insertNewSession(@Bind(SESSION_ID) sessionId: Int,
                         @Bind(PAT_ID) patId: String,
                         @Bind(DATE) date: Timestamp)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllSessions(): List<Session>

    @SqlUpdate("DELETE FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun deleteSessionBySessionId(@Bind(SESSION_ID) sessionId: Int)

}

