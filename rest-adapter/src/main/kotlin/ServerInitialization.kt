import com.github.kittinunf.fuel.httpGet
import config.Services
import model.Serializer.klaxon
import spark.Spark

fun waitInitAndNotifyToMicroSession(instanceId: Int) {
    Spark.awaitInitialization()
    "http://localhost:${Services.SESSION.port}/session/acknowledge/$instanceId"
            .httpGet().responseString().third.fold(success = {
                println(klaxon.parse<ResponseMessage>(it)!!.message)
    }, failure = { println("http://localhost:${Services.SESSION.port}/session/acknowledge/$instanceId \nricevuto un errore $it") })
}