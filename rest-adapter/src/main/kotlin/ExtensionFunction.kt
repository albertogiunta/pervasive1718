import spark.Response

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
    this.body(ResponseMessage(201, "Resource not found").toJson())
    return this.body()
}

fun Response.hostNotFound(): String {
    this.status(404)
    this.type("application/json")
    this.body(ResponseMessage(502, "Host Not Found").toJson())
    return this.body()
}

fun Response.hostNotFound(host: String): String {
    this.status(404)
    this.type("application/json")
    this.body(ResponseMessage(502, "Host Not Found for host: $host").toJson())
    return this.body()
}

