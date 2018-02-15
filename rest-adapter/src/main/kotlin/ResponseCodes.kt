import spark.Response
import utils.toJson

data class ResponseMessage(val id: Int, val message: String)

fun Response.ok(): String {
    this.status(200)
    this.type("application/json")
    this.body(ResponseMessage(200, "Ok").toJson())
    return this.body()
}

fun Response.okCreated(): String {
    this.status(201)
    this.type("application/json")
    this.body(ResponseMessage(201, "Ok Created").toJson())
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
    this.body(ResponseMessage(404, "Resource not found").toJson())
    return this.body()
}

fun Response.internalServerError(error: String): String {
    this.status(500)
    this.type("application/json")
    this.body(ResponseMessage(500, "Internal server error. Details: $error").toJson())
    return this.body()
}

fun Response.resourceNotAvailable(host: String): String {
    this.status(503)
    this.type("application/json")
    this.body(ResponseMessage(503, "Service $host not available.").toJson())
    return this.body()
}
