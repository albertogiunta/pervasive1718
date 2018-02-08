package dao

import Params.Session.ROOM_ID
import Params.Session.SESSION_ID
import Params.Session.TABLE_NAME
import model.Session
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface SessionDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($SESSION_ID, $ROOM_ID) VALUES (:$SESSION_ID, :$ROOM_ID)")
    fun insertNewSession(@Bind(SESSION_ID) sessionId: Int,
                         @Bind(ROOM_ID) roomId: Int)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllSessions(): List<Session>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun selectSessionBySessionId(@Bind(SESSION_ID) sessionId: Int): List<Session>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ROOM_ID = (:$ROOM_ID)")
    fun selectSessionByRoomId(@Bind(ROOM_ID) roomId: Int): List<Session>

    @SqlQuery("DELETE FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun deleteSessionBySessionId(@Bind(SESSION_ID) sessionId: Int)

    @SqlQuery("DELETE FROM $TABLE_NAME WHERE $ROOM_ID = (:$ROOM_ID)")
    fun deleteSessionByRoomId(@Bind(ROOM_ID) roomId: Int)

}

