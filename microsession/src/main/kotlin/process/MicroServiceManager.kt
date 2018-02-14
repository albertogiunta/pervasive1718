package process

import config.Services
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class MicroServiceManager (val workingDir: File){

    val sessionsMap = mutableMapOf<String, Map<Services, Pair<Process, URL>>>()

    val instanceHandler = object : InstanceHandler<Services, String> {
        override fun new(service: Services, sessionID: String): Pair<Process, URL> {
            val url = URL(
                    Services.Utils.Protocols.http,
                    Services.Utils.defaultHost, service.port,
                    "/session/$sessionID${service.wsPath}"
            )

            return "java -jar ${service.executableName} ${service.port}".runCommand(workingDir) to url
        }
    }

    fun newSession(sessionID: String) {
        if (!sessionsMap.containsKey(sessionID)) {
            val map = mutableMapOf<Services, Pair<Process, URL>>()

            map[Services.NOTIFIER] = instanceHandler.new(Services.NOTIFIER, sessionID)

            sessionsMap[sessionID] = map.toMap()
        }
    }

    fun closeSession(sessionID: String) {
        if (sessionsMap.containsKey(sessionID)) {
            sessionsMap[sessionID]!!.forEach { _, (process, _) ->
                process.destroy()
            }
            sessionsMap.remove(sessionID)
        }
    }

    private fun String.runCommand(workingDir: File) : Process =
        ProcessBuilder(*split(" ").toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

}

interface InstanceHandler<X, Y> {

    fun new(service: X, sessionID: Y) : Pair<Process, URL>
}

fun main(args: Array<String>) {
    val joiner = StringJoiner(
            System.getProperty("file.separator"),
            System.getProperty("file.separator"),
            System.getProperty("file.separator")
    )
    val w = File("${System.getProperty("user.dir")}${joiner.add("micronotifier").add("build").add("libs")}")

    println(w)
    val m = MicroServiceManager(w)

    val e = m.instanceHandler.new(Services.NOTIFIER, "666")

    println(e.second)
    println(e.first.waitFor(5000L, TimeUnit.MILLISECONDS))

    e.first.destroy()
}