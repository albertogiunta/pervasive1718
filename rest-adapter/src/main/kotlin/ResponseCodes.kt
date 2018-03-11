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

fun Response.okAccepted(): String {
    this.status(202)
    this.type("application/json")
    this.body(ResponseMessage(202, "Ok Accepted").toJson())
    return this.body()
}

fun Response.badRequest(additionalInformation: String?): String {
    this.status(400)
    this.type("application/json")
    this.body(ResponseMessage(400, "Request body was unacceptable. $additionalInformation?").toJson())
    return this.body()
}

fun Response.notFound(additionalInformation: String?): String {
    this.status(404)
    this.type("application/json")
    this.body(ResponseMessage(404, "Resource not found. $additionalInformation?").toJson())
    return this.body()
}

fun Response.internalServerError(errorDetails: String): String {
    this.status(500)
    this.type("application/json")
    this.body(ResponseMessage(500, "Internal server error. Details: $errorDetails").toJson())
    return this.body()
}

fun Response.resourceNotAvailable(host: String, errorDetails: String): String {
    this.status(503)
    this.type("application/json")
    this.body(ResponseMessage(503, "Service $host not available. Additional info: $errorDetails").toJson())
    return this.body()
}