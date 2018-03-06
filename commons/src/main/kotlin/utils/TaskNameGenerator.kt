package utils

import model.Status
import model.Task
import java.sql.Timestamp
import java.util.*

fun Task.Companion.newTask(sid: Int, aid: Int, ocf: String,
                           start: Timestamp = Timestamp(Date().time),
                           end: Timestamp? = null,
                           status: Int = Status.RUNNING.id): Task {
    return Task(
            name = generateTaskName(sid, aid, ocf, start),
            sessionId = sid,
            operatorCF = ocf,
            startTime = start,
            endTime = end,
            activityId = aid,
            statusId = status
    )
}

fun Task.Companion.generateTaskName(sid: Int, aid: Int, ocf: String, start: Timestamp): String =
        "S${sid}A${aid}${ocf}$start"
                .replace("-", "")
                .replace(":", "")
                .replace(".", "")

fun Task.Companion.generateTaskName(task: Task): String =
        generateTaskName(task.sessionId, task.activityId, task.operatorCF, task.startTime)
