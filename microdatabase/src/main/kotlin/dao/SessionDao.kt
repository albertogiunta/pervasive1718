package dao

import Params.Session.END_DATE
import Params.Session.INSTANCE_ID
import Params.Session.PAT_ID
import Params.Session.SESSION_ID
import Params.Session.START_DATE
import Params.Session.TABLE_NAME
import model.Session
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Timestamp

interface SessionDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($PAT_ID, $START_DATE, $INSTANCE_ID) VALUES (:$PAT_ID, :$START_DATE, :$INSTANCE_ID)")
    @GetGeneratedKeys
    fun insertNewSession(@Bind(PAT_ID) patId: String,
                         @Bind(START_DATE) startDate: Timestamp,
                         @Bind(INSTANCE_ID) microServiceInstanceId: Int): Session


    @SqlUpdate("UPDATE $TABLE_NAME SET $END_DATE = (:$END_DATE) WHERE $SESSION_ID = (:$SESSION_ID)")
    fun closeSessionBySessionId(@Bind(SESSION_ID) sessionId: Int,
                                @Bind(END_DATE) endDate: Timestamp)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllSessions(): List<Session>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $END_DATE IS NULL")
    fun selectAllOpenSessions(): List<Session>

}

