package process

import config.Services
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class MicroServiceManager (val workingDir: String, val projectName: String = "pervasive1718"){

    val sessionsMap = mutableMapOf<String, Map<Services, Pair<Process, URL>>>()

    val instanceHandler = object : InstanceHandler<Services, String> {
        override fun new(service: Services, sessionID: String): Pair<Process, URL> {

            val dir = workingDir.replaceAfter(projectName,"")
            val url = URL(
                    Services.Utils.Protocols.http,
                    Services.Utils.defaultHost, service.port,
                    "/session/$sessionID${service.wsPath}"
            )
            val workingModule = StringJoiner(System.getProperty("file.separator"))
                    .add(dir)
                    .add(service.module)
                    .add("build")
                    .add("libs")
                    .toString()

            return "java -jar ${service.executableName} ${service.port}".runCommand(File(workingModule)) to url
        }
    }

    fun newSession(sessionID: String) {
        Services.values().forEach { newService(it,sessionID) }
    }

    fun newService(service: Services, sessionID: String){

        lateinit var map: MutableMap<Services, Pair<Process, URL>>
        when(!sessionsMap.containsKey(sessionID)){
            true -> map = mutableMapOf()
            false -> map = sessionsMap[sessionID]!!.toMutableMap()
        }
        map[service] = instanceHandler.new(service,sessionID)
        sessionsMap[sessionID] = map.toMap()
    }

    fun closeSession(sessionID: String) {
        if (sessionsMap.containsKey(sessionID)) {
            sessionsMap[sessionID]!!.forEach { _, (process, _) ->
                process.destroy()
            }
            sessionsMap.remove(sessionID)
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

    fun new(service: X, sessionID: Y) : Pair<Process, URL>
}

fun main(args: Array<String>) {

    val w = System.getProperty("user.dir")

    val m = MicroServiceManager(w)

    val e = m.instanceHandler.new(Services.NOTIFIER, "666")

    println(e.second)
    println(e.first.waitFor(5000L, TimeUnit.MILLISECONDS))

    e.first.destroy()
}