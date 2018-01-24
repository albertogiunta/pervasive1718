import spark.Spark.get
import spark.Spark.port

fun main(args: Array<String>) {
    port(8080)

    get("/hello") { req, res -> "Hello World" }
}