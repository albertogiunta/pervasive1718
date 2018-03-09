import com.github.kittinunf.fuel.httpGet
import config.Services
import config.Services.Utils.WAIT_TIME_BEFORE_THE_NEXT_REQUEST
import model.Serializer.klaxon
import spark.Spark

fun waitInitAndNotifyToMicroSession(serviceName: String, instanceId: Int) {
    var configNotCompleted = true
    println("[$serviceName] looping & waiting on microsession")
    Spark.awaitInitialization()
    while (configNotCompleted) {
        "http://localhost:${Services.SESSION.port}/sessions/acknowledge/$instanceId"
            .httpGet()
            .responseString()
            .third
            .fold(
                success = {
                    println("[$serviceName] SUCCESSFUL communication with microsession | ${klaxon.parse<ResponseMessage>(it)!!.message}")
                    configNotCompleted = false
                }, failure = {
                    println("[$serviceName] UNSUCCESSFUL communication with microsession")
                    Thread.sleep(WAIT_TIME_BEFORE_THE_NEXT_REQUEST)
                })
    }
}