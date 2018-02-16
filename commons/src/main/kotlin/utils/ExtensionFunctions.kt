package utils

import LifeParameters
import com.google.gson.GsonBuilder
import config.Services
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

fun Services.calculatePort(args: Array<String>) = if ( args.isEmpty()) this.port else args[0].toInt()

fun LifeParameters.acronymWithPort(port: Int) = this.acronym + port