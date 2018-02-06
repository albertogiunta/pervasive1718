import Connection.ADDRESS
import Connection.API_PORT
import Connection.LOCAL_HOST
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.github.kittinunf.fuel.httpPost
import com.google.gson.JsonObject
import org.junit.AfterClass
import org.junit.Test

class DatabaseSubscriberTest {
    companion object {
        //Remember to start the RabbitMQ broker on the specified host
        // otherwise the system throw a ConnectionException
        private val connector: BrokerConnector
        private val completeURL: String = PROTOCOL + PROTOCOL_SEPARATOR + ADDRESS + PORT_SEPARATOR + API_PORT + "/" + Connection.API + "/" + Params.Log.TABLE_NAME + "/add"

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

        val json = JsonObject()
        json.addProperty("healthParameterId", 123)
        json.addProperty("healthParameterValue", 1212)
        print(completeURL)
        sub.subscribe(LifeParameters.HEART_RATE, sub.createStringConsumer {
            json.addProperty(Params.Log.NAME, it)
            val (_, _, result) = completeURL.httpPost().body(json.toString()).responseString()
            print(result)
            print(it)
        })

        Thread.sleep(5000)

        val pub = Thread({
            val pub = RabbitMQPublisher(connector)
            pub.publish("Test 1", LifeParameters.HEART_RATE)
        })
        pub.start()

        Thread.sleep(2000)

    }
}