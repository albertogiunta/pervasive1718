object MicroserviceBootUtils {
    fun startMicroservice(path: String): Process {
        var res = Runtime.getRuntime().exec("java -jar \"" + MicroservicesPaths.microMonitor + "\"")
        return res
    }
}

fun Process.killAtParentDeath(): Process {
    Runtime.getRuntime().addShutdownHook(Thread({ this.destroy() }))
    return this
}

object MicroservicesPaths {
    private val separator = System.getProperty("file.separator").get(0)
    private val internalPath = separator + "build" + separator + "libs" + separator
    val microMonitor = System.getProperty("user.dir").dropLastWhile { it != separator } + "micromonitor" + internalPath + "runMicroMonitor-0.1.jar"
    val microDatabase = System.getProperty("user.dir").dropLastWhile { it != separator } + "microdatabase" + internalPath + "runMicroDatabase-0.1.jar"
    val microTask = System.getProperty("user.dir").dropLastWhile { it != separator } + "microtask" + internalPath + "runMicroTask-0.1.jar"
    val microNotifier = System.getProperty("user.dir").dropLastWhile { it != separator } + "micronotifier" + internalPath + "runMicroNotifier-0.1.jar"
    val microVisors = System.getProperty("user.dir").dropLastWhile { it != separator } + "microvisors" + internalPath + "runMicroVisors-0.1.jar"
    val microSession = System.getProperty("user.dir").dropLastWhile { it != separator } + "microsession" + internalPath + "runMicroSession-0.1.jar"
}