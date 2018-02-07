import junit.framework.Assert.assertEquals
import logic.*
import logic.ServerControllerImpl.Companion.HOST
import logic.ServerControllerImpl.Companion.TASK_ROOT_PATH
import logic.ServerControllerImpl.Companion.WS_PORT
import networking.WSTaskClient
import networking.WSTaskServer
import org.junit.Test
import spark.Spark
import spark.kotlin.port
import java.net.URI
import java.sql.Timestamp
import java.util.*

class TaskMessagingTest {

    companion object {
        private lateinit var controller: Controller

        init {
            initServer()
                .also { Thread.sleep(3000) }
                .also { controller = ServerControllerImpl.INSTANCE }
        }

        private fun initServer() {
            port(WS_PORT)
            Spark.webSocket(TASK_ROOT_PATH, WSTaskServer::class.java)
            Spark.init()
        }

        private fun initClient(): WSTaskClient =
            WSTaskClient(URI("$HOST$WS_PORT$TASK_ROOT_PATH"))
    }

    @Test
    fun memberAddedTest() {
        val initialSize = controller.members.size

        // member
        Thread({
            initClient()
                .also { it.connect() }
                .also { Thread.sleep(1000) }
                .also { it.send(TaskPayload(Member(1, "Member"), Operation.ADD_MEMBER, Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))).toJson()) }
        })
            .also { it.start() }
            .also { Thread.sleep(3000) }
            .also { assertEquals(controller.members.size, initialSize + 1) }
    }

    @Test
    fun taskAssignmentTest() {
        // member
        Thread({
            initClient()
                .also { it.connect() }
                .also { Thread.sleep(1000) }
                .also { it.send(TaskPayload(Member(1, "Member"), Operation.ADD_MEMBER, Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))).toJson()) }

        })
            .also { it.start() }
            .also { Thread.sleep(1000) }

        // leader
        Thread({
            initClient()
                .also { it.connect() }
                .also { Thread.sleep(1000) }
                .also { it.send(TaskPayload(Member(1, "Member"), Operation.ADD_TASK, Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))).toJson()) }
        })
            .also { it.start() }
            .also { Thread.sleep(5000) }
    }
}