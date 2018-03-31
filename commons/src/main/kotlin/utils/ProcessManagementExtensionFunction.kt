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

fun Process.killKaazingGatewayOnFatherDeath() = {
    val closeChildThread = object : Thread() {
        override fun run() {
            if (SystemInfo.isWindows()) {
                "pkill /IM " + KaazingGatewayStarter.WINDOWS_EXECUTABLE_NAME + " /F"
            }
            this@killKaazingGatewayOnFatherDeath.destroy()
        }
    }

    Runtime.getRuntime().addShutdownHook(closeChildThread)
}