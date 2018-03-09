package utils

/**
 * This class is used to create the final report during a session ending
 * */
object ReportGenerator {

    val separator = System.getProperty("file.separator").get(0)
    val DEFAULT_PATH = PathGetter.getRootPath() + separator + "sessionReport" + separator
    val DEFAULT_FILE_NAME = "Report Session N "
    val fileNameRegex = """${DEFAULT_FILE_NAME}.[0-9]*""".toRegex()

    fun generateFinalReport() {
        /*val reportInJson: JsonValue = "http://localhost:8100/api/session/633/report".httpGet().responseString().third.fold(
                success = {
                    instance[session.second] = false
                    sessions.removeAll { it.first.sessionId == sessionId }
                    sManager.closeSession(session.second.toString())
                    return response.ok()
                },
                failure = { return it.toJson() }
        )*/

    }
}

fun main(args: Array<String>) {

    //è una lista di due stringhe : la prima stringa è un json array i cui elementi sono chiamati TaskReportEntry, mentre gli altri sono sempre dei JsonArray di tipo LogReportEntry

}
