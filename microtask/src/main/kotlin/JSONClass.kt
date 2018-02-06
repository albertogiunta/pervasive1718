import com.google.gson.Gson
import logic.Member
import logic.Task
import logic.ontologies.Operation

data class JSONClass(val doctor: Member, val operation: Operation, val task: Task)

fun JSONClass.toJson(): String = Gson().toJson(this)