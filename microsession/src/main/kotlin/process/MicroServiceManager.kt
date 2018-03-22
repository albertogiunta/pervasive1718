package process

import config.Services
import model.MicroServiceHook
import utils.PathGetter
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MicroServiceManager {

    private val sessionsMap = ConcurrentHashMap<String, ConcurrentHashMap<Services, Pair<Process, MicroServiceHook>>>()

    private val instanceHandler =
            object : InstanceHandler<Services, String, Boolean, Process, MicroServiceHook> {

                override fun new(service: Services, slotId: String, startIndependently: Boolean): Pair<Process, MicroServiceHook> {

                    val dir = PathGetter.getJarGenerationDirectoryPath()

                    val defaultURL = MicroServiceHook(service, slotId, Services.Utils.defaultHost, "")

                    val workingModule = StringJoiner(System.getProperty("file.separator"))
                            .add(dir)
                            .toString()

                    return "java -jar ${service.executableName} ${slotId.toInt()} -si $startIndependently"
                            .runCommand(File(workingModule)) to defaultURL
                }
            }

    fun newSession(slotId: String) {
        Services.valuesWithoutSession().forEach {
            println("Try to start " + it.executableName)
            newService(it, slotId)
        }
    }

    fun newService(service: Services, slotId: String, startIndependently: Boolean = false){

        val map: ConcurrentHashMap<Services, Pair<Process, MicroServiceHook>> = when (!sessionsMap.containsKey(slotId)) {
            true -> ConcurrentHashMap()
            false -> sessionsMap[slotId]!!
        }
        map[service] = instanceHandler.new(service, slotId, startIndependently)
        sessionsMap[slotId] = map
    }

    fun setHook(service: Services, slotId: String, hook: MicroServiceHook) {
        if (sessionsMap.contains(slotId) && sessionsMap[slotId]!!.contains(service)) {
            val (p, _) = sessionsMap[slotId]!![service]!!
            sessionsMap[slotId]!![service] = p to hook
        }
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

interface InstanceHandler<in X, in Y, in Z, out W, out V> {
    fun new(service: X, slotId: Y, startIndependently: Z) : Pair<W, V>
}