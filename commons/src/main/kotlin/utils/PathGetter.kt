package utils

object PathGetter {

    val PROJECT_NAME = "pervasive1718"
    val CONFIG_FILE = "config.json"
    val JARS_GENERATION_DIRECTORY_NAME = "generatedJars"

    fun getRootPath(): String {
        val usrDir = System.getProperty("user.dir")
        val separator = System.getProperty("file.separator").get(0)
        return usrDir.split(PROJECT_NAME)[0] + PROJECT_NAME + separator
    }

    fun getConfigPath(): String {
        return getRootPath() + CONFIG_FILE
    }

    fun getJarGenerationDirectoryPath(): String {
        val separator = System.getProperty("file.separator").get(0)
        return getRootPath() + JARS_GENERATION_DIRECTORY_NAME + separator
    }
}

fun main(array: Array<String>) {
    println(PathGetter.getRootPath())

    println(PathGetter.getConfigPath())
}