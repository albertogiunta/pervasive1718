@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.OperatorDao
import model.Operator
import okCreated
import spark.Request
import spark.Response
import utils.toJson
import java.sql.SQLException

object OperatorApi {

    /**
     * Inserts a new role inside of the Operator table
     */
    fun addOperator(request: Request, response: Response): String {
        val operator: Operator = Klaxon().parse<Operator>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<OperatorDao, SQLException>(OperatorDao::class.java)
        {
            it.insertNewOperator(
                operator.operatorCF,
                operator.name,
                operator.surname,
                operator.roleId,
                operator.isActive)
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