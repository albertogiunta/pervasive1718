import com.google.gson.Gson
import model.ResponseMessage
import spark.Response

fun getGsonInstance(): Gson = Gson()

fun Any.toJson(): String = getGsonInstance().toJson(this)

fun Response.okCreated(): String {
    this.status(201)
    this.type("application/json")
    this.body(ResponseMessage(201, "Ok").toJson())
    return this.body()
}

fun Response.badRequest(): String {
    this.status(400)
    this.type("application/json")
    this.body(ResponseMessage(400, "Request body was unacceptable").toJson())
    return this.body()
}

fun Response.notFound(): String {
    this.status(404)
    this.type("application/json")
    this.body(ResponseMessage(201, "Resource not found").toJson())
    return this.body()
}
