package microservices.microdb.dao

import microservices.microdb.model.Operator
import microservices.microdb.utils.Params.Operator.ID
import microservices.microdb.utils.Params.Operator.IS_ACTIVE
import microservices.microdb.utils.Params.Operator.NAME
import microservices.microdb.utils.Params.Operator.ROLE_ID
import microservices.microdb.utils.Params.Operator.SURNAME
import microservices.microdb.utils.Params.Operator.TABLE_NAME
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface OperatorDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $SURNAME, $ROLE_ID, $IS_ACTIVE) VALUES (:$NAME, :$SURNAME, :$ROLE_ID, :$IS_ACTIVE)")
    fun insertNewOperator(@Bind(NAME) name: String,
                          @Bind(SURNAME) surname: String,
                          @Bind(ROLE_ID) roleId: Int,
                          @Bind(IS_ACTIVE) isActive: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllOperators(): List<Operator>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectOperatorById(@Bind(ID) id: Int): List<Operator>


}