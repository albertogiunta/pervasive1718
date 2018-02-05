package dao

import Params.Boundary.ACTIVITY_ID
import Params.Boundary.HEALTH_PARAMETER_ID
import Params.Boundary.ID
import Params.Boundary.LOWERBOUND
import Params.Boundary.TABLE_NAME
import Params.Boundary.UPPERBOUND
import model.Boundary
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface BoundaryDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($HEALTH_PARAMETER_ID, $ACTIVITY_ID, $UPPERBOUND, $LOWERBOUND) VALUES (:$HEALTH_PARAMETER_ID, :$ACTIVITY_ID, :$UPPERBOUND, :$LOWERBOUND)")
    fun insertNewStatus(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                        @Bind(ACTIVITY_ID) activityId: Int,
                        @Bind(UPPERBOUND) upperBound: Double,
                        @Bind(LOWERBOUND) lowerBound: Double)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllStatuses(): List<Boundary>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectStatusById(@Bind(ID) id: Int): List<Boundary>


}