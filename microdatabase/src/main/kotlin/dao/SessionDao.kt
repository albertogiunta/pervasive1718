package dao

import Params
import Params.Session.END_DATE
import Params.Session.INSTANCE_ID
import Params.Session.LEADER_CF
import Params.Session.PATIENT_CF
import Params.Session.SESSION_ID
import Params.Session.START_DATE
import Params.Session.TABLE_NAME
import Params.Task.END_TIME
import Params.Task.OPERATOR_CF
import Params.Task.START_TIME
import model.Session
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Timestamp

interface SessionDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($PATIENT_CF, $LEADER_CF, $START_DATE, $INSTANCE_ID) " +
            "VALUES (:$PATIENT_CF, :$LEADER_CF, :$START_DATE, :$INSTANCE_ID)")
    @GetGeneratedKeys
    fun insertNewSession(@Bind(PATIENT_CF) patientCF: String,
                         @Bind(LEADER_CF) leaderCF: String,
                         @Bind(START_DATE) startDate: Timestamp,
                         @Bind(INSTANCE_ID) microServiceInstanceId: Int): Session

    @SqlUpdate("INSERT INTO $TABLE_NAME($SESSION_ID, $PATIENT_CF, $LEADER_CF, $START_DATE, $INSTANCE_ID) " +
            "VALUES (:$SESSION_ID, :$PATIENT_CF, :$LEADER_CF, :$START_DATE, :$INSTANCE_ID)")
    @GetGeneratedKeys
    fun insertNewSessionWithId(@Bind(SESSION_ID) id : Int,
                               @Bind(PATIENT_CF) patientCF: String,
                               @Bind(LEADER_CF) leaderCF: String,
                               @Bind(START_DATE) startDate: Timestamp,
                               @Bind(INSTANCE_ID) microServiceInstanceId: Int): Session

    @SqlUpdate("UPDATE $TABLE_NAME SET $END_DATE = (:$END_DATE) WHERE $SESSION_ID = (:$SESSION_ID)")
    fun closeSessionBySessionId(@Bind(SESSION_ID) sessionId: Int,
                                @Bind(END_DATE) endDate: Timestamp)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllSessions(): List<Session>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $END_DATE IS NULL AND $LEADER_CF = (:$LEADER_CF)")
    fun selectAllOpenSessionsByLeaderCF(@Bind(LEADER_CF) leaderCF: String): List<Session>

    @SqlQuery("select array_to_json(array_agg(row_to_json(t))) from (" +
                "select " +
                    "S.${Params.Session.SESSION_ID} as sessionId, " +
                    "T.${Params.Task.TASK_NAME} as taskStrId, " +
                    "S.${Params.Session.LEADER_CF}, " +
                    "S.${Params.Session.PATIENT_CF}, " +
                    "A.${Params.Activity.ACRONYM}, " +
                    "A.${Params.Activity.NAME} as activityName, " +
                    "A.hps as relatedHealthParameters, " +
                    "T.$START_TIME, T.$END_TIME, O.$OPERATOR_CF " +
                "from ${Params.Session.TABLE_NAME} as S " +
                "left outer join ${Params.Task.TABLE_NAME} as T on T.${Params.Task.SESSION_ID} = S.${Params.Session.SESSION_ID} " +
                "left outer join (" +
                    "select A.${Params.Activity.ID}, A.${Params.Activity.ACRONYM}, A.${Params.Activity.NAME}, array_agg(distinct HP.${Params.HealthParameter.NAME}) as hps " +
                    "from ${Params.Activity.TABLE_NAME} as A " +
                    "inner join ${Params.HealthParameter.TABLE_NAME} as HP on A.${Params.Activity.HEALTH_PARAMETER_IDS} @> Array[HP.${Params.HealthParameter.ID}] " +
                    "group by 1, 2, 3" +
                ") as A on T.${Params.Task.ACTIVITY_ID} = A.${Params.Activity.ID} " +
                "left outer join ${Params.Operator.TABLE_NAME} as O on T.${Params.Task.OPERATOR_CF} = O.${Params.Operator.CF} " +
                "where A.${Params.Activity.NAME} is not NULL and S.${Params.Session.SESSION_ID} = (:$SESSION_ID) " +
                "order by T.${Params.Task.START_TIME}" +
            ") t;")
    fun getTaskReport(@Bind(SESSION_ID) sessionId: Int) : String

    @SqlQuery("select array_to_json(array_agg(row_to_json(t))) from (" +
            "select " +
            "S.${Params.Session.SESSION_ID} as sessionid, " +
            "S.${Params.Session.LEADER_CF}, " +
            "S.${Params.Session.PATIENT_CF}, " +
            "L.${Params.Log.LOG_TIME} as Date, " +
            "HP.${Params.HealthParameter.ACRONYM}, " +
            "HP.${Params.HealthParameter.NAME} as HealthParameter, " +
            "L.${Params.Log.HEALTH_PARAMETER_VALUE} as ActualValue " +
            "from ${Params.Session.TABLE_NAME} as S " +
            "inner join ${Params.Log.TABLE_NAME} as L on L.${Params.Log.SESSION_ID} = S.${Params.Session.SESSION_ID} " +
            "inner join ${Params.HealthParameter.TABLE_NAME} as HP on L.${Params.Log.HEALTH_PARAMETER_ID} = HP.${Params.HealthParameter.ID} " +
            "where S.${Params.Session.SESSION_ID} = (:$SESSION_ID)" +
            "order by 4" +
            ") t;")
    fun getLogReport(@Bind(SESSION_ID) sessionId: Int) : String
}

