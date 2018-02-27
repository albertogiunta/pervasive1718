package dao

import Params.Operator.ID
import Params.Operator.IS_ACTIVE
import Params.Operator.NAME
import Params.Operator.ROLE_ID
import Params.Operator.SURNAME
import Params.Operator.TABLE_NAME
import Params.Task.OPERATOR_CF
import model.Operator
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface OperatorDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($OPERATOR_CF, $NAME, $SURNAME, $ROLE_ID, $IS_ACTIVE) VALUES (:$OPERATOR_CF, :$NAME, :$SURNAME, :$ROLE_ID, :$IS_ACTIVE)")
    fun insertNewOperator(@Bind(OPERATOR_CF) operatorCF: String,
                          @Bind(NAME) name: String,
                          @Bind(SURNAME) surname: String,
                          @Bind(ROLE_ID) roleId: Int,
                          @Bind(IS_ACTIVE) isActive: Boolean)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllOperators(): List<Operator>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectOperatorById(@Bind(ID) id: Int): List<Operator>


}