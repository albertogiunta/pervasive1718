package dao

import model.Status
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params.Status.ACTIVITY_ID
import utils.Params.Status.HEALTH_PARAMETER_ID
import utils.Params.Status.ID
import utils.Params.Status.LOWERBOUND
import utils.Params.Status.TABLE_NAME
import utils.Params.Status.UPPERBOUND

interface StatusDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($HEALTH_PARAMETER_ID, $ACTIVITY_ID, $UPPERBOUND, $LOWERBOUND) VALUES (:$HEALTH_PARAMETER_ID, :$ACTIVITY_ID, :$UPPERBOUND, :$LOWERBOUND)")
    fun insertNewStatus(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                        @Bind(ACTIVITY_ID) activityId: Int,
                        @Bind(UPPERBOUND) upperBound: Double,
                        @Bind(LOWERBOUND) lowerBound: Double)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllStatuses(): List<Status>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectStatusById(@Bind(ID) id: Int): List<Status>


}