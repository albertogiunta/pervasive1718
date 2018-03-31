package utils

object SystemInfo {

    val FILE_SEPARATOR = System.getProperty("file.separator").get(0)

    val OS = System.getProperty("os.name").toLowerCase()

    fun isWindows(): Boolean = OS.contains("windows")

    fun isLinux(): Boolean = OS.contains("linux")

    fun isMac(): Boolean = OS.contains("mac os x")
}