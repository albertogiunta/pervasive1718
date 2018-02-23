import config.Services
import spark.Spark
import com.github.kittinunf.fuel.httpGet
import model.Serializer.klaxon

fun waitInitAndNotifyToMicroSession(instanceId: Int) {
    Spark.awaitInitialization()
    "http://localhost:${Services.SESSION.port}/session/acknowledge/$instanceId"
            .httpGet().responseString().third.fold(success = {
                println(klaxon.parse<ResponseMessage>(it)!!.message)
            }, failure = { println("ho ricevuto un errore $it") })
}