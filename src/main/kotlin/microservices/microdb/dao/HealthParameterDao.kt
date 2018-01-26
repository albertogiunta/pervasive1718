package microservices.microdb.dao

import microservices.microdb.model.HealthParameter
import microservices.microdb.utils.Params.HealthParameter.ID
import microservices.microdb.utils.Params.HealthParameter.NAME
import microservices.microdb.utils.Params.HealthParameter.SIGNATURE
import microservices.microdb.utils.Params.HealthParameter.TABLE_NAME
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

