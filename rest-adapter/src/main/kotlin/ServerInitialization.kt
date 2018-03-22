import com.github.kittinunf.fuel.httpPost
import config.Services
import config.Services.Utils.WAIT_TIME_BEFORE_THE_NEXT_REQUEST
import model.MicroServiceHook
import model.Serializer.klaxon
import spark.Spark
import utils.toJson
import java.net.InetAddress

fun waitInitAndNotifyToMicroSession(serviceName: String, instanceId: Int) {
    var configNotCompleted = true
    println("[$serviceName] looping & waiting on microsession")

    Spark.awaitInitialization()
    while (configNotCompleted) {
        "http://${Connection.ADDRESS}:${Services.SESSION.port}/${Params.Session.API_NAME}/acknowledge/$instanceId"
            .httpPost()
            .body(MicroServiceHook(
                    Services.getByExecutableName(serviceName),
                    instanceId.toString(),
                    InetAddress.getLocalHost().hostAddress,
                    InetAddress.getLocalHost().hostName
                ).toJson()
            ).responseString()
            .third
            .fold(success = {
                println("[$serviceName] SUCCESSFUL communication with microsession | ${klaxon.parse<ResponseMessage>(it)!!.message}")
                configNotCompleted = false
            }, failure = {
                println("[$serviceName] UNSUCCESSFUL communication with microsession")
                Thread.sleep(WAIT_TIME_BEFORE_THE_NEXT_REQUEST)
            })
    }
}