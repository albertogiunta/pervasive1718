package microservices.microdb.controllers.api

import microservices.microdb.dao.HealthParameterDao
import microservices.microdb.model.HealthParameter
import microservices.microdb.utils.JdbiConfiguration
import microservices.microdb.utils.Params
import microservices.microdb.utils.okCreated
import microservices.microdb.utils.toJson
import spark.Request
import spark.Response
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