package utils

import com.google.gson.GsonBuilder
import config.Services
import model.*

object GsonInitializer {
    val gson = GsonBuilder().create()
    fun toJson(src: Any?): String = gson.toJson(src)
    fun <T> fromJson(json : String, clazz: Class<T>) : T = gson.fromJson(json, clazz)
}

fun Any?.asJson(): String = GsonInitializer.toJson(this)
fun Any.toJson(): String = GsonInitializer.toJson(this)

fun Task.toVisibleTask(member: Member, activityName: String) =
    VisibleTask(this.id, activityName, member.userCF, "nome a caso", "cognome a caso")

fun Task.toAugmentedTask(activityList: List<Activity>): AugmentedTask {
    val currentActivity = activityList.first { it -> it.id == this.activityId }
    return AugmentedTask(this, currentActivity.healthParameterIds
            .map { id -> LifeParameters.Utils.getByID(id) }, currentActivity.name)
}

fun Services.calculatePort(args: Array<String>) = if (args.isEmpty() || args[0] == "") this.port else this.port + args[0].toInt()

fun LifeParameters.acronymWithSession(args: Array<String>) =
        this.acronym + (if (args.isEmpty()) "" else args[0].toInt())

fun LifeParameters.acronymWithSession(sessionID: Int) = this.acronym + sessionID