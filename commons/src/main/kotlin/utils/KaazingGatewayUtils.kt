package utils

import java.io.File

object KaazingGatewayUtils {

    private val GATEWAY_VERSION = "5.7.3"
    private val GATEWAY_DIRECTORY_NAME = "kaazing-enterprise-gateway-" + GATEWAY_VERSION
    private val EXECUTABLE_LOCATION = GATEWAY_DIRECTORY_NAME +
            PathGetter.SYSTEM_SEPARATOR +
            "bin" +
            PathGetter.SYSTEM_SEPARATOR

    private val UNIX_EXECUTABLE_NAME = "gateway.start"
    private val WINDOWS_EXECUTABLE_NAME = "gateway.start.bat"

    private val UNIX_WORKING_MODULE = PathGetter.getKaazingGatewayPath() +
            PathGetter.SYSTEM_SEPARATOR +
            "unix" +
            PathGetter.SYSTEM_SEPARATOR +
            EXECUTABLE_LOCATION

    private val WINDOWS_WORKING_MODULE = PathGetter.getKaazingGatewayPath() +
            PathGetter.SYSTEM_SEPARATOR +
            "windows" +
            PathGetter.SYSTEM_SEPARATOR +
            EXECUTABLE_LOCATION

    val onProcessFatherDeath: () -> Unit = {
        if (SystemInfo.isWindows()) {
            "pkill /IM " + KaazingGatewayUtils.WINDOWS_EXECUTABLE_NAME + " /F".runCommandIn(File(WINDOWS_WORKING_MODULE))
        }
    }

    fun startGateway(): Process {
        var workingModule = ""
        var command = ""

        if (SystemInfo.isLinux() || SystemInfo.isMac()) {
            workingModule = UNIX_WORKING_MODULE
            command = "./" + UNIX_EXECUTABLE_NAME
        } else if (SystemInfo.isWindows()) {
            workingModule = WINDOWS_WORKING_MODULE
            command = "cmd.exe /c start /B \"\" " + WINDOWS_EXECUTABLE_NAME
        }

        return command.runCommandIn(File(workingModule))
    }
}
