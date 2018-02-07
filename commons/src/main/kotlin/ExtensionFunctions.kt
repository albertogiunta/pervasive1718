import com.google.gson.Gson

fun getGsonInstance(): Gson = Gson()

fun Any.toJson(): String = getGsonInstance().toJson(this)
