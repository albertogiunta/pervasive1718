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
    val microMonitor = System.getProperty("user.dir")
            .dropLastWhile { it != '/' } + "micromonitor/build/libs/runMicroMonitor-0.1.jar"
    val microDatabase = System.getProperty("user.dir").dropLastWhile { it != '/' } + "microdatabase/build/libs/runMicroMonitor-0.1.jar"
    val microTask = System.getProperty("user.dir").dropLastWhile { it != '/' } + "microtask/build/libs/runMicroMonitor-0.1.jar"
    val microNotifier = System.getProperty("user.dir").dropLastWhile { it != '/' } + "micronotifier/build/libs/runMicroMonitor-0.1.jar"
    val microVisors = System.getProperty("user.dir").dropLastWhile { it != '/' } + "microvisors/build/libs/runMicroMonitor-0.1.jar"
}