import Connection.ADDRESS
import Connection.API_PORT
import Connection.LOCAL_HOST
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import Params.Log.HEALTH_PARAMETER_VALUE
import Params.Log.TABLE_NAME
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.JsonObject
import model.KlaxonDate
import model.Log
import model.dateConverter

import org.junit.AfterClass
import org.junit.Test
import java.io.StringReader
import java.util.*
import kotlin.math.absoluteValue

class DatabaseSubscriberTest {
    companion object {
        //Remember to start the RabbitMQ broker on the specified host
        // otherwise the system throw a ConnectionException
        private val connector: BrokerConnector
        private val addString: String = PROTOCOL + PROTOCOL_SEPARATOR + ADDRESS + PORT_SEPARATOR + API_PORT + "/" + Connection.API + "/" + TABLE_NAME + "/add"
        private val readString: String = PROTOCOL + PROTOCOL_SEPARATOR + ADDRESS + PORT_SEPARATOR + API_PORT + "/" + Connection.API + "/" + TABLE_NAME + "/" + Params.HealthParameter.TABLE_NAME + "/"

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
    fun writeSingleData(){

        val sub = RabbitMQSubscriber(connector)
        val randomId = Math.abs(Random().nextInt(500))
        val json = JsonObject()
        json.addProperty("healthParameterId",randomId)
        json.addProperty("healthParameterValue",1212)
        sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer {
            json.addProperty(Params.Log.NAME, it)
            val (_, _, result) = addString.httpPost().body(json.toString()).responseString()
            result.fold(success = {
                println(it)
            }, failure = {
                println(String(it.errorData))
            })
        })

        Thread.sleep(6000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            pub.publish("Test 1", LifeParameters.HEART_RATE)
        })
        pub.start()

        Thread.sleep(4000)


        lateinit var listResult :List<Log>
        val (_, _, result) = (readString + randomId).httpGet().responseString()
        result.fold(success = {
            val klaxon = Klaxon()
                    .fieldConverter(KlaxonDate::class, dateConverter)
            JsonReader(StringReader(it)).use { reader ->
                listResult = arrayListOf<Log>()
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

        println(listResult)
        assert(listResult.firstOrNull{it.healthParameterId == randomId} != null)

    }

}