package utils

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import model.LogReportEntry
import model.TaskReportEntry
import java.io.File
import java.io.Reader
import java.io.StringReader

/**
 * This class is used to create the final report during a session ending
 * */
object ReportGenerator {

    private val separator = System.getProperty("file.separator")[0]
    private val DEFAULT_PATH = PathGetter.getRootPath() + separator + "sessionReport" + separator
    private const val DEFAULT_FILE_NAME = "Report Session N "
    private const val FILE_EXTENSION = ".txt"

    fun generateFinalReport(sessionId: String) {

        val fileName = DEFAULT_FILE_NAME + sessionId
        File(DEFAULT_PATH).mkdirs()
        val reportFile = File(DEFAULT_PATH + fileName + FILE_EXTENSION)
        val restAddress = "http://localhost:8100/api/sessions/$sessionId/report"
        val response = restAddress.httpGet().responseString()

        response.third.fold(success = {
            val klaxon = Klaxon()
                .fieldConverter(KlaxonLifeParameterList::class, lifeParameterListConverter)
                .fieldConverter(KlaxonDate::class, dateConverter)
            JsonReader(StringReader(it) as Reader).use { reader ->
                reader.beginArray {
                    val sTaskReport = reader.nextString()
                    val taskReport = klaxon.parseArray<TaskReportEntry>(sTaskReport)!!
                    val sLogReport = reader.nextString()
                    val logReportEntry = klaxon.parseArray<LogReportEntry>(sLogReport)!!
                    writeReportOnFile(taskReport, logReportEntry, reportFile)
                    println("Generating the final report => \"${fileName + FILE_EXTENSION}\" in $DEFAULT_PATH")
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }

    private fun writeReportOnFile(taskReport: List<TaskReportEntry>, logReport: List<LogReportEntry>, reportFile: File) {
        reportFile.printWriter().use { out ->
            out.println("TASK REPORT")
            taskReport.forEach {
                val line = "Leader: ${it.leaderCF} | Operator: ${it.operatorCF} | Patient: ${it.patientCF} | Activity: ${it.activityName} | StartTime: ${it.startTime} -> EndTIme: ${it.endTime}"
                out.println(line)
                line.forEach { out.print("-") }
                out.print("\n")
            }
            out.println()
            out.println("LOG REPORT")
            out.println()
            with(logReport.first()) {
                out.println("Leader: ${this.leaderCF} | Patient: ${this.patientCF}")
            }
            logReport.forEach {
                out.println("DateTime: ${it.dateTime} | Health Parameter: ${it.healthParameter} => ${it.hpValue}")
            }

        }
    }

}

fun main(args: Array<String>) {
    ReportGenerator.generateFinalReport("799")
}
