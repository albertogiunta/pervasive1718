package utils

object PathGetter {

    val PROJECT_ROOT = "pervasive1718"
    val CONFIG_FILE = "config.json"

    fun getRootPath(): String {
        val usrDir = System.getProperty("user.dir")
        val separator = System.getProperty("file.separator").get(0)
        return usrDir.split(PROJECT_ROOT)[0] + PROJECT_ROOT + separator
    }

    fun getConfigPath(): String {
        val usrDir = System.getProperty("user.dir")
        val separator = System.getProperty("file.separator").get(0)
        return usrDir.split(PROJECT_ROOT)[0] + PROJECT_ROOT + separator + CONFIG_FILE
    }
}

fun main(array: Array<String>) {
    println(PathGetter.getRootPath())

    println(PathGetter.getConfigPath())
}