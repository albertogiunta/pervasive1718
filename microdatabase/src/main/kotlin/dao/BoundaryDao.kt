package dao

import Params.Boundary.ACTIVITY_ID
import Params.Boundary.HEALTH_PARAMETER_ID
import Params.Boundary.ID
import Params.Boundary.ITS_GOOD
import Params.Boundary.LIGHT_WARNING_OFFSET
import Params.Boundary.LOWERBOUND
import Params.Boundary.MAX_AGE
import Params.Boundary.MIN_AGE
import Params.Boundary.STATUS
import Params.Boundary.TABLE_NAME
import Params.Boundary.UPPERBOUND
import model.Boundary
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface BoundaryDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($HEALTH_PARAMETER_ID, $ACTIVITY_ID, $UPPERBOUND, $LOWERBOUND, $LIGHT_WARNING_OFFSET, $STATUS, $ITS_GOOD, $MIN_AGE, $MAX_AGE) VALUES (:$HEALTH_PARAMETER_ID, :$ACTIVITY_ID, :$UPPERBOUND, :$LOWERBOUND, :$LIGHT_WARNING_OFFSET, :$STATUS, :$ITS_GOOD, :$MIN_AGE, :$MAX_AGE)")
    fun insertNewStatus(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                        @Bind(ACTIVITY_ID) activityId: Int,
                        @Bind(UPPERBOUND) upperBound: Double,
                        @Bind(LOWERBOUND) lowerBound: Double,
                        @Bind(LIGHT_WARNING_OFFSET) lightWarningOffset: Double,
                        @Bind(STATUS) status: String,
                        @Bind(ITS_GOOD) itsGood: Boolean,
                        @Bind(MIN_AGE) minAge: Double,
                        @Bind(MAX_AGE) maxAge: Double)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllStatuses(): List<Boundary>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectStatusById(@Bind(ID) id: Int): List<Boundary>


}