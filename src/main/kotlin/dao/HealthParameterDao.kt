package dao

import model.HealthParameter
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params.HealthParameter.ID
import utils.Params.HealthParameter.NAME
import utils.Params.HealthParameter.SIGNATURE
import utils.Params.HealthParameter.TABLE_NAME

interface HealthParameterDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $SIGNATURE) VALUES (:$NAME, :$SIGNATURE)")
    fun insertNewHealthParameter(@Bind(NAME) name: String, @Bind(SIGNATURE) signature: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllHealthParameters(): List<HealthParameter>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectHealthParameterById(@Bind(ID) id: Int): List<HealthParameter>

}

