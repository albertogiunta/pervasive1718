package main.kotlin.microdb.controllers.api

import JdbiConfiguration
import Params
import main.kotlin.microdb.dao.OperatorDao
import main.kotlin.microdb.model.Operator
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object OperatorApi {

    /**
     * Inserts a new role inside of the Operator table
     */
    fun addOperator(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<OperatorDao, SQLException>(OperatorDao::class.java)
        {
            it.insertNewOperator(
                    request.queryParams(Params.Operator.NAME),
                    request.queryParams(Params.Operator.SURNAME),
                    request.queryParams(Params.Operator.ROLE_ID).toInt(),
                    request.queryParams(Params.Operator.IS_ACTIVE))
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the operators
     */
    fun getAllOperators(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Operator>, OperatorDao, SQLException>(OperatorDao::class.java)
        { it.selectAllOperators() }
                .toJson()
    }

    /**
     * Retrieves a specific operator based on the passed operator ID
     */
    fun getOperatorById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Operator>, OperatorDao, SQLException>(OperatorDao::class.java)
        { it.selectOperatorById(request.params(Params.Operator.ID).toInt()) }
                .toJson()
    }

}