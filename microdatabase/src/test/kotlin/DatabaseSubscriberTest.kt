import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import Connection.REMOTE_HOST
import Params.Log.TABLE_NAME
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.JsonObject
import config.ConfigLoader
import config.Services
import model.Log
import org.junit.AfterClass
import org.junit.Test
import utils.handlingGetResponse
import java.util.*

class DatabaseSubscriberTest {

    companion object {
        /* Remember to start the RabbitMQ broker on the specified host
         * otherwise the system throw a ConnectionException.
         * For this reason, use REMOTE_HOST as default.
         *
         **/
        private val connector: BrokerConnector
        private val addString: String
        private val allString: String
        private val readString: String

        lateinit var listResult: List<Log>

        init {

            ConfigLoader("../config.json").load()
            addString = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/$TABLE_NAME/add"
            allString = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/$TABLE_NAME/all"
            readString = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/$TABLE_NAME/${Params.HealthParameter.TABLE_NAME}/"

            BrokerConnector.init(REMOTE_HOST)
            connector = BrokerConnector.INSTANCE
            MicroDatabaseBootstrap.init(Services.DATA_BASE.port)
        }

        @AfterClass
        @JvmStatic
        fun closeConnection() {
            BrokerConnector.INSTANCE.close()
        }
    }

    @Test
    fun writeSingleData() {

        val sub = RabbitMQSubscriber(connector)
        val randomId = Math.abs(Random().nextInt(500))
        val json = JsonObject()
        json.addProperty("healthParameterId", randomId)
        json.addProperty("healthParameterValue", 1212)
        println(addString)
        sub.subscribe(LifeParameters.HEART_RATE.acronym, sub.createStringConsumer {
            json.addProperty(Params.Log.NAME, it)
            makePost(addString, json)
        })

        Thread.sleep(6000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            pub.publish("Test 1", LifeParameters.HEART_RATE.acronym)
        })
        pub.start()

        Thread.sleep(4000)
        listResult = handlingGetResponse(makeGet(readString + randomId))

        sub.unsubscribe(LifeParameters.HEART_RATE.acronym)

        println(listResult)
        assert(listResult.firstOrNull { it.healthParameterId == randomId } != null)
    }

    @Test
    fun writeMultipleData() {

        listResult = handlingGetResponse(makeGet(allString))
        val initialListSize = listResult.size

        val json = JsonObject()
        json.addProperty("healthParameterId", 12)
        json.addProperty("healthParameterValue", 1212)
        val subCode = Thread {
            val sub = RabbitMQSubscriber(connector)
            LifeParameters.values().forEach { X ->
                sub.subscribe(X.acronym, sub.createStringConsumer {
                    json.addProperty(Params.Log.NAME, it)
                    makePost(addString, json)
                })
            }
        }
        subCode.start()

        Thread.sleep(6000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            LifeParameters.values().forEach {
                for (i in 0 until 3) {
                    Thread.sleep(1000)
                    pub.publish(i.toString(), it.acronym)
                }
            }
        })
        pub.start()
        Thread.sleep(50000)


        listResult = handlingGetResponse(makeGet(allString))

        println(listResult.size)
        println(initialListSize)
        assert(listResult.size == (initialListSize + 18))

    }

    private fun makePost(string: String, data: JsonObject): Triple<Request, Response, Result<String, FuelError>> {
        return string.httpPost().body(data.toString()).responseString().also {
            it.third.fold(success = {
                println(it)
            }, failure = {
                println(String(it.errorData))
            })
        }
    }

    private fun makeGet(string: String): Triple<Request, Response, Result<String, FuelError>> {
        return string.httpGet().responseString()
    }
}