package utils

import com.google.gson.GsonBuilder
import model.Member
import model.Priority
import model.Task
import model.VisibleTask

object GsonInitializer {
    val gson = GsonBuilder().create()
    fun toJson(src: Any?): String = gson.toJson(src)
}

fun Any?.asJson(): String = GsonInitializer.toJson(this)
fun Any.toJson(): String = GsonInitializer.toJson(this)

fun Task.toVisibleTask(member: Member) =
        VisibleTask(this.id, "il name del task va tolto", Priority.HIGH, member.id, member.name, member.name)