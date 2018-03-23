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
        if (isLinux() || isMac()) {
            val workingModule = PathGetter.KAAZING_GATEWAYS_DEFAULT_DIRECTORY_NAME + "unix/" + EXECUTABLE_LOCATION
            "./gateway.start".runCommand(File(workingModule))
        } else if (isWindows()) {
            val workingModule = PathGetter.KAAZING_GATEWAYS_DEFAULT_DIRECTORY_NAME + "unix/" + EXECUTABLE_LOCATION
            "gateway.start.bat".runCommand(File(workingModule))
        }
    }

    private fun isWindows(): Boolean = os.contains("Windows")

    private fun isLinux(): Boolean = os.contains("Linux")

    private fun isMac(): Boolean = os.contains("Mac OS X")
}
