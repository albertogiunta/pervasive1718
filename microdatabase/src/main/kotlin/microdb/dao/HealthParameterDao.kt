package main.kotlin.microdb.dao

import Params.HealthParameter.ID
import Params.HealthParameter.NAME
import Params.HealthParameter.SIGNATURE
import Params.HealthParameter.TABLE_NAME
import main.kotlin.microdb.model.HealthParameter
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface HealthParameterDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $SIGNATURE) VALUES (:$NAME, :$SIGNATURE)")
    fun insertNewHealthParameter(@Bind(NAME) name: String, @Bind(SIGNATURE) signature: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllHealthParameters(): List<HealthParameter>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectHealthParameterById(@Bind(ID) id: Int): List<HealthParameter>

}

