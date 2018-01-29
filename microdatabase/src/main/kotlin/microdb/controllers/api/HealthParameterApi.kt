package microdb.controllers.api

import JdbiConfiguration
import Params
import microdb.dao.HealthParameterDao
import microdb.model.HealthParameter
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object HealthParameterApi {

    /**
     * Inserts a new health parameter inside of the Health Parameters table
     */
    fun addHealthParameter(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<HealthParameterDao, SQLException>(HealthParameterDao::class.java)
        {
            it.insertNewHealthParameter(
                    request.queryParams(Params.HealthParameter.NAME),
                    request.queryParams(Params.HealthParameter.SIGNATURE))
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the health parameters
     */
    fun getAllHealthParameters(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<HealthParameter>, HealthParameterDao, SQLException>(HealthParameterDao::class.java)
        { it.selectAllHealthParameters() }
                .toJson()
    }

    /**
     * Retrieves a specific health paramters based on the passed health parameter ID
     */
    fun getHealthParameterById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<HealthParameter>, HealthParameterDao, SQLException>(HealthParameterDao::class.java)
        { it.selectHealthParameterById(request.params(Params.HealthParameter.ID).toInt()) }
                .toJson()
    }
}