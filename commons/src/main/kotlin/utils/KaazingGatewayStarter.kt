package utils

import java.io.File

object KaazingGatewayStarter {

    private val GATEWAY_VERSION = "5.7.3"
    private val GATEWAY_DIRECTORY_NAME = "kaazing-enterprise-gateway-" + GATEWAY_VERSION
    private val EXECUTABLE_LOCATION = GATEWAY_DIRECTORY_NAME +
            PathGetter.SYSTEM_SEPARATOR +
            "bin" +
            PathGetter.SYSTEM_SEPARATOR

    val UNIX_EXECUTABLE_NAME = "gateway.start"
    val WINDOWS_EXECUTABLE_NAME = "gateway.start.bat"

    fun startGateway(): Process {
        var dir = ""
        var command = ""
        if (SystemInfo.isLinux() || SystemInfo.isMac()) {
            dir =  "unix"
            command = "./" + UNIX_EXECUTABLE_NAME
        } else if (SystemInfo.isWindows()) {
            dir =  "windows"
            command = "cmd.exe /c start \"\" " + WINDOWS_EXECUTABLE_NAME + "/B"
        }

        val workingModule = PathGetter.getKaazingGatewayPath() +
                PathGetter.SYSTEM_SEPARATOR +
                dir +
                PathGetter.SYSTEM_SEPARATOR +
                EXECUTABLE_LOCATION

        return command.runCommandIn(File(workingModule))
    }
}
