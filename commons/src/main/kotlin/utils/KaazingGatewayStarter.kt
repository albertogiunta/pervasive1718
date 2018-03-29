package utils

import java.io.File

object KaazingGatewayStarter {

    private val os = System.getProperty("os.name").toLowerCase()
    private val GATEWAY_VERSION = "5.7.3"
    private val GATEWAY_DIRECTORY_NAME = "kaazing-enterprise-gateway-" + GATEWAY_VERSION
    private val EXECUTABLE_LOCATION = GATEWAY_DIRECTORY_NAME +
            PathGetter.SYSTEM_SEPARATOR +
            "bin" +
            PathGetter.SYSTEM_SEPARATOR

    fun startGateway() {
        var dir = ""
        var command = ""
        if (isLinux() || isMac()) {
            dir =  "unix"
            command ="./gateway.start"
        } else if (isWindows()) {
            dir =  "windows"
            command = "gateway.start.bat"
        }

        val workingModule = PathGetter.getKaazingGatewayPath() +
                PathGetter.SYSTEM_SEPARATOR +
                dir +
                PathGetter.SYSTEM_SEPARATOR +
                EXECUTABLE_LOCATION

        command.runCommandIn(File(workingModule)).killOnFatherDeath()
    }

    private fun isWindows(): Boolean = os.contains("windows")

    private fun isLinux(): Boolean = os.contains("linux")

    private fun isMac(): Boolean = os.contains("mac os x")
}
