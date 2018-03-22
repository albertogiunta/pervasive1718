package process

import Params
import config.ConfigLoader
import config.Services
import utils.PathGetter
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class MicroServiceManager {

    private val sessionsMap = mutableMapOf<String, Map<Services, Pair<Process, URL>>>()

    val instanceHandler = object : InstanceHandler<Services, String, Boolean> {

        override fun new(service: Services, slotId: String, startIndependently: Boolean): Pair<Process, URL> {

            val dir = PathGetter.getJarGenerationDirectoryPath()

            val url = URL(
                    Services.Utils.Protocols.http,
                    Services.Utils.defaultHost, slotId.toInt(),
                    "/${Params.Session.API_NAME}/$slotId${service.wsPath}"
            )

            val workingModule = StringJoiner(System.getProperty("file.separator"))
                    .add(dir)
                    .toString()

            return "java -jar ${service.executableName} ${slotId.toInt()} -si $startIndependently"
                    .runCommand(File(workingModule)) to url
        }
    }

    fun newSession(slotId: String) {
        Services.valuesWithoutSession().forEach {
            println("Try to start " + it.executableName)
            newService(it, slotId)
        }
    }

    fun newService(service: Services, slotId: String, startIndependently: Boolean = false){

        lateinit var map: MutableMap<Services, Pair<Process, URL>>
        when(!sessionsMap.containsKey(slotId)){
            true -> map = mutableMapOf()
            false -> map = sessionsMap[slotId]!!.toMutableMap()
        }
        map[service] = instanceHandler.new(service, slotId, startIndependently)
        sessionsMap[slotId] = map.toMap()
    }

    fun closeSession(slotId: String) {
        if (sessionsMap.containsKey(slotId)) {
            sessionsMap[slotId]!!.forEach { _, (process, _) ->
                process.destroy()
            }
            sessionsMap.remove(slotId)
        }
    }

    private fun String.runCommand(dir: File) : Process =
        ProcessBuilder(*split(" ").toTypedArray())
                .directory(dir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

}

interface InstanceHandler<X, Y, Z> {

    fun new(service: X, slotId: Y, startIndependently: Z) : Pair<Process, URL>
}

@Suppress("UNUSED_VARIABLE")
fun main(args: Array<String>) {

    ConfigLoader().load(args)

    val w = System.getProperty("user.dir")

    val m = MicroServiceManager()

    val e = m.instanceHandler.new(Services.NOTIFIER, "666", true)

    println(e.second)
    println(e.first.waitFor(5000L, TimeUnit.MILLISECONDS))

    e.first.destroy()
}