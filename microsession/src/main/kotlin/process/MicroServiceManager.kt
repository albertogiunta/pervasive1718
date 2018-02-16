package process

import config.ConfigLoader
import config.Services
import utils.PathGetter
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class MicroServiceManager {

    val sessionsMap = mutableMapOf<String, Map<Services, Pair<Process, URL>>>()

    val instanceHandler = object : InstanceHandler<Services, String> {

        override fun new(service: Services, slotId: String): Pair<Process, URL> {

            val dir = PathGetter.getRootPath()

            val dynamicPort = service.port + slotId.toInt()

            val url = URL(
                    Services.Utils.Protocols.http,
                    Services.Utils.defaultHost, dynamicPort,
                    "/session/$slotId${service.wsPath}"
            )

            val workingModule = StringJoiner(System.getProperty("file.separator"))
                    .add(dir)
                    .add(service.module)
                    .add("build")
                    .add("libs")
                    .toString()

            return "java -jar ${service.executableName} $dynamicPort".runCommand(File(workingModule)) to url
        }
    }

    fun newSession(slotId: String) {
        Services.values().forEach { newService(it,slotId) }
    }

    fun newService(service: Services, slotId: String){

        lateinit var map: MutableMap<Services, Pair<Process, URL>>
        when(!sessionsMap.containsKey(slotId)){
            true -> map = mutableMapOf()
            false -> map = sessionsMap[slotId]!!.toMutableMap()
        }
        map[service] = instanceHandler.new(service,slotId)
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

interface InstanceHandler<X, Y> {

    fun new(service: X, slotId: Y) : Pair<Process, URL>
}

fun main(args: Array<String>) {

    ConfigLoader().load(args)

    val w = System.getProperty("user.dir")

    val m = MicroServiceManager()

    val e = m.instanceHandler.new(Services.NOTIFIER, "666")

    println(e.second)
    println(e.first.waitFor(5000L, TimeUnit.MILLISECONDS))

    e.first.destroy()
}