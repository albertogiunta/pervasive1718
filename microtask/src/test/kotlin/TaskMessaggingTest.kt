import logic.Member
import logic.Task
import logic.ontologies.Operation
import logic.ontologies.Status
import networking.WSTaskClient
import networking.WSTaskServer
import org.junit.Test
import spark.Spark
import spark.kotlin.port
import utils.WSParams
import java.net.URI
import java.sql.Timestamp
import java.util.*

class TaskMessaggingTest {

    companion object {
        init {
            port(WSParams.WS_PORT)
            Spark.webSocket(WSParams.TASK_ROOT_PATH, WSTaskServer::class.java)
            Spark.init()
        }
    }

    @Test
    fun taskAssegnation() {

        Thread.sleep(4000)

        val member = Thread ({
            val client = WSTaskClient(URI("${WSParams.HOST}${WSParams.WS_PORT}${WSParams.TASK_ROOT_PATH}"))
            client.connect()
            Thread.sleep(1000)
            client.send(JSONClass(Member(1, "Member"), Operation.ADD_MEMBER, Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))).toJson())
        })
        member.start()

        Thread.sleep(1000)

        val leader = Thread({
            val client = WSTaskClient(URI("${WSParams.HOST}${WSParams.WS_PORT}${WSParams.TASK_ROOT_PATH}"))
            client.connect()
            Thread.sleep(1000)
            client.send(JSONClass(Member(1, "Member"), Operation.ADD_TASK, Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))).toJson())
        })
        leader.start()
        //TODO assertion
        Thread.sleep(5000)
    }

}