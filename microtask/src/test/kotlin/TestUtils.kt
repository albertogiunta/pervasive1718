import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import logic.Member
import logic.Task
import logic.TaskOperation
import logic.TaskPayload
import java.io.StringReader
import java.util.ArrayList

fun addLeaderThread(memberId: Int): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient()
                .sendMessage(TaskPayload(Member(memberId, "Leader"), TaskOperation.ADD_LEADER, Task.emptyTask()).toJson())
    })
}
fun addMemberThread(memberId: Int): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient()
                .sendMessage(TaskPayload(Member(memberId, "Member"), TaskOperation.ADD_MEMBER, Task.emptyTask()).toJson())
    })
}

fun addTaskThread(task: Task, member: Member): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().sendMessage(TaskPayload(member, TaskOperation.ADD_TASK, task).toJson())
    })
}

fun removeTaskThread(task: Task): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().sendMessage(TaskPayload(Member.emptyMember(), TaskOperation.REMOVE_TASK, task).toJson())
    })
}

fun changeTaskStatus(task: Task): Thread {
    return Thread({
        initializeConnectionWithTaskWSClient().sendMessage(TaskPayload(Member.emptyMember(), TaskOperation.CHANGE_TASK_STATUS, task).toJson())
    })
}


fun initializeConnectionWithTaskWSClient(): WSClient {
    return WSClientInitializer.init(WSClient(URIFactory.getTaskURI())).also { Thread.sleep(1000) }
}

inline fun <reified A> handlingGetResponse(triplet: Triple<Request, Response, Result<String, FuelError>>):List<A> {
    lateinit var listResult : List<A>
    triplet.third.fold(success = {
        val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        JsonReader(StringReader(it)).use { reader ->
            listResult = arrayListOf()
            reader.beginArray {
                while (reader.hasNext()) {
                    val data = klaxon.parse<A>(reader)!!
                    (listResult as ArrayList<A>).add(data)
                }
            }
        }
    }, failure = {
        println(String(it.errorData))
    })
    return listResult
}