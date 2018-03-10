package utils

import Params
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import model.LogReportEntry
import model.TaskReportEntry
import java.io.Reader
import java.io.StringReader

/**
 * This class is used to create the final report during a session ending
 * */
object ReportGenerator {

    val separator = System.getProperty("file.separator").get(0)
    val DEFAULT_PATH = PathGetter.getRootPath() + separator + "sessionReport" + separator
    val DEFAULT_FILE_NAME = "Report Session N "
    val fileNameRegex = """${DEFAULT_FILE_NAME}.[0-9]*""".toRegex()

    fun generateFinalReport() {

        val fileName = DEFAULT_FILE_NAME + Params.Session.SESSION_ID
        //val reportInJson: JsonValue =
        val response = "http://localhost:8100/api/sessions/799/report".httpGet().responseString()
        response.third.fold(
                success = {
                    //println(it)
                },
                failure = { println(it) }
        )

        var taskReportEntry: TaskReportEntry? = null
        var logReportEntry: LogReportEntry? = null

        response.third.fold(success = {
            val klaxon = Klaxon()
                    .fieldConverter(KlaxonLifeParameterList::class, lifeParameterListConverter)
                    .fieldConverter(KlaxonDate::class, dateConverter)
            //println(it + "\n")
            JsonReader(StringReader(it) as Reader).use { reader ->
                reader.beginArray {
                    val sTaskReportEntry: String = reader.nextString().removePrefix("[").removeSuffix("]")
                    println(sTaskReportEntry + "\n\n\n")
                    taskReportEntry = klaxon.parse<TaskReportEntry>(sTaskReportEntry)!!
//                    val sLogReportEntry = reader.nextString()
//                    println(sLogReportEntry)
//                    logReportEntry = klaxon.parse<LogReportEntry>(sLogReportEntry)!!
//                    logReportEntry?.run { println(this.toString()) }
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }
}

fun main(args: Array<String>) {

    //è una lista di due stringhe : la prima stringa è un json array i cui elementi sono chiamati TaskReportEntry, mentre gli altri sono sempre dei JsonArray di tipo LogReportEntry

    ReportGenerator.generateFinalReport()
}
