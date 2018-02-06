package logic

import KlaxonDate
import logic.ontologies.Status
import java.sql.Timestamp

data class Task(val id: Int, val name: String, var status: Status, @KlaxonDate val startTime: Timestamp, @KlaxonDate val endTime: Timestamp)