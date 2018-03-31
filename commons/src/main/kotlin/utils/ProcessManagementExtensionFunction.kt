package utils

import java.io.File


/**
 * Extension function to start in a quick way a bash command in a specified directory
 *
 * @param dir directory where the command is located
 */
fun String.runCommandIn(dir: File): Process =
        ProcessBuilder(*split(" ").toTypedArray())
                .directory(dir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

/**
 * Extension function that attach a shutdownhook to the specified process
 *
 * @param cleanFunction higher order function called before the process killing.
 *
 * */
fun Process.killOnFatherDeath(cleanFunction: () -> Unit) = {
    val closeChildThread = object : Thread() {
        override fun run() {
            cleanFunction()
            this@killOnFatherDeath.destroy()
        }
    }

    Runtime.getRuntime().addShutdownHook(closeChildThread)
}