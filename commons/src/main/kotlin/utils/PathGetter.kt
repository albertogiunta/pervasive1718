package utils

object PathGetter {

    val SYSTEM_SEPARATOR = System.getProperty("file.separator").get(0)
    val PROJECT_NAME = "pervasive1718"
    val CONFIG_FILE = "config.json"
    val JARS_GENERATION_DIRECTORY_NAME = "generatedJars"
    val KAAZING_GATEWAYS_DEFAULT_DIRECTORY_NAME = "kaazing-gateways"

    fun getRootPath(): String {
        val usrDir = System.getProperty("user.dir")
        return usrDir.split(PROJECT_NAME)[0] + PROJECT_NAME + SYSTEM_SEPARATOR
    }

    fun getConfigPath(): String {
        return getRootPath() + CONFIG_FILE
    }

    fun getJarGenerationDirectoryPath(): String {
        return getRootPath() + JARS_GENERATION_DIRECTORY_NAME + SYSTEM_SEPARATOR
    }

    fun getKaazingGatewayPath(): String {
        return getRootPath() + KAAZING_GATEWAYS_DEFAULT_DIRECTORY_NAME + SYSTEM_SEPARATOR
    }
}

fun main(array: Array<String>) {
    println(PathGetter.getRootPath())

    println(PathGetter.getConfigPath())
}