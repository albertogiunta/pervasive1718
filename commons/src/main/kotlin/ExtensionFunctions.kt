import GsonInitializer.gson
import com.google.gson.Gson

object GsonInitializer {
    val gson = Gson()
}

fun Any.toJson(): String = gson.toJson(this)
