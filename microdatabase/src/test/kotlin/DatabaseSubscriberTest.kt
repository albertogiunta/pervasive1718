import Connection.ADDRESS
import Connection.API_PORT
import Connection.LOCAL_HOST
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import Params.Log.TABLE_NAME
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.JsonObject
import model.Log
import org.junit.AfterClass
import org.junit.Test
import java.io.StringReader
import java.util.*

class DatabaseSubscriberTest {
    companion object {
        //Remember to start the RabbitMQ broker on the specified host
        // otherwise the system throw a ConnectionException
        private val connector: BrokerConnector
        private val addString: String = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR$API_PORT/${Connection.API}/$TABLE_NAME/add"
        private val allString: String = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR$API_PORT/${Connection.API}/$TABLE_NAME/all"
        private val readString: String = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR$API_PORT/${Connection.API}/$TABLE_NAME/${Params.HealthParameter.TABLE_NAME}/"

        lateinit var listResult: List<Log>

        init {
            BrokerConnector.init(LOCAL_HOST)
            connector = BrokerConnector.INSTANCE
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
        sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer {
            json.addProperty(Params.Log.NAME, it)
            makePost(addString, json)
        })

        Thread.sleep(6000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            pub.publish("Test 1", LifeParameters.HEART_RATE)
        })
        pub.start()

        Thread.sleep(4000)
        handlingGetResponse(makeGet(readString + randomId))

        println(listResult)
        assert(listResult.firstOrNull { it.healthParameterId == randomId } != null)
    }

    @Test
    fun writeMultipleData() {

        handlingGetResponse(makeGet(allString))
        val initialListSize = listResult.size

        val json = JsonObject()
        json.addProperty("healthParameterId", 12)
        json.addProperty("healthParameterValue", 1212)
        val subCode = Thread {
            val sub = RabbitMQSubscriber(connector)
            LifeParameters.values().forEach { X ->
                sub.subscribe(X, sub.createStringConsumer {
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
                    pub.publish(i.toString(), it)
                }
            }
        })
        pub.start()
        Thread.sleep(50000)


        handlingGetResponse(makeGet(allString))

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

    private fun handlingGetResponse(triplet: Triple<Request, Response, Result<String, FuelError>>) {
        triplet.third.fold(success = {
            val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
            JsonReader(StringReader(it)).use { reader ->
                listResult = arrayListOf()
                reader.beginArray {
                    while (reader.hasNext()) {
                        val log = klaxon.parse<Log>(reader)!!
                        (listResult as ArrayList<Log>).add(log)
                    }
                }
            }
        }
            , failure = {
                println(String(it.errorData))
            })
    }
}