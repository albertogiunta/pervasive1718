package dao

import Params.HealthParameter.ID
import Params.HealthParameter.NAME
import Params.HealthParameter.ACRONYM
import Params.HealthParameter.TABLE_NAME
import model.HealthParameter
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface HealthParameterDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $ACRONYM) VALUES (:$NAME, :$ACRONYM)")
    fun insertNewHealthParameter(@Bind(NAME) name: String, @Bind(ACRONYM) signature: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllHealthParameters(): List<HealthParameter>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectHealthParameterById(@Bind(ID) id: Int): List<HealthParameter>

}

