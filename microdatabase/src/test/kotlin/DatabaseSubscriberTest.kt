import Connection.ADDRESS
import Connection.API_PORT
import Connection.LOCAL_HOST
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR

import com.github.kittinunf.fuel.httpPost
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import org.junit.AfterClass
import org.junit.Test

class DatabaseSubscriberTest {
    companion object {
        //Remember to start the RabbitMQ broker on the specified host
        // otherwise the system throw a ConnectionException
        private val connector: BrokerConnector
        private val completeURL: String = PROTOCOL + PROTOCOL_SEPARATOR + ADDRESS + PORT_SEPARATOR + API_PORT + "/" + Params.Log.TABLE_NAME + "/add"

        init {
            BrokerConnector.init(LOCAL_HOST)
            connector = BrokerConnector.INSTANCE
        }

        @AfterClass
        @JvmStatic
        fun closeConnection() {
            Thread.sleep(4000)
            BrokerConnector.INSTANCE.close()
        }
    }
    @Test
    fun writeSingleData(){


        val json = JsonObject()
        json.addProperty(Params.Log.HEALTH_PARAMETER_ID,123)
        json.addProperty(Params.Log.HEALTH_PARAMETER_VALUE,1212)
        println(json.toString())
        val sub = Thread({
            val sub = RabbitMQSubscriber(connector)
            print(completeURL)
            sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer {
                X -> {
                    json.addProperty(Params.Log.NAME,X)
                    val (_, _, result) =
                    completeURL.httpPost()
                            .body(json.toString())
                            .responseString()
                    result.fold(success = {
                        println(it)
                    }, failure = {
                        println(String(it.errorData))
                    })
                }
            })
        })
        sub.start()

        Thread.sleep(2000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            pub.publish("Test 1", LifeParameters.HEART_RATE)
        })
        pub.start()
    }
}