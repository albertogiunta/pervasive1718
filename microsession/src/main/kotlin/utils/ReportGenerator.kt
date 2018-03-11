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

    val separator = System.getProperty("file.separator").get(0)
    val DEFAULT_PATH = PathGetter.getRootPath() + separator + "sessionReport" + separator
    val DEFAULT_FILE_NAME = "Report Session N "
    val FILE_EXTENSION = ".txt"

    fun generateFinalReport(sessionId: String) {

        val fileName = DEFAULT_FILE_NAME + sessionId
        val reportFile = File(DEFAULT_PATH + fileName + FILE_EXTENSION)
        val restAddress = "http://localhost:8100/api/sessions/${sessionId}/report"
        val response = restAddress.httpGet().responseString()

        response.third.fold(success = {
            val klaxon = Klaxon()
                    .fieldConverter(KlaxonLifeParameterList::class, lifeParameterListConverter)
                    .fieldConverter(KlaxonDate::class, dateConverter)
            //println(it + "\n")
            JsonReader(StringReader(it) as Reader).use { reader ->
                reader.beginArray {
                    val sTaskReport = reader.nextString()
                    var taskReport = klaxon.parseArray<TaskReportEntry>(sTaskReport)!!
                    val sLogReport = reader.nextString()
                    var logReportEntry = klaxon.parseArray<LogReportEntry>(sLogReport)!!
                    writeReportOnFile(taskReport, logReportEntry, reportFile)
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
                var line = "Leader: ${it.leaderCF} | Operator ${it.operatorCF} | Patient ${it.patientCF} | Activity ${it.activityName} | StartTime ${it.startTime} -> EndTIme ${it.endTime}"
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
