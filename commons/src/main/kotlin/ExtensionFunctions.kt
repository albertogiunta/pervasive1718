import com.google.gson.GsonBuilder

object GsonInitializer {
    val gson = GsonBuilder().create()
    fun toJson(src: Any?): String = gson.toJson(src)
}

fun Any.toJson(): String = GsonInitializer.toJson(this)
fun Any?.asJson(): String = GsonInitializer.toJson(this)