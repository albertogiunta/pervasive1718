
import junit.framework.Assert.assertEquals
import logic.*
import networking.WSTaskClient
import networking.WSTaskServer
import org.junit.Test
import spark.Spark
import spark.kotlin.port
import java.sql.Timestamp
import java.util.*

class TaskMessagingTest {

    companion object {
        private var serverController: ServerController

        init {
            initServer()
            Thread.sleep(3000)
            serverController = ServerControllerImpl.INSTANCE
        }

        private fun initServer() {
            port(WSParams.WS_PORT)
            Spark.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            Spark.init()
        }

        private fun initClient(): WSTaskClient = WSClientInitializer.init(WSTaskClient(URIFactory.getTaskURI()))
    }

    @Test
    fun memberAddedTest() {
        val initialSize = serverController.members.size

        // member
        val member = Thread({
            val client = initClient()
            client.connect()
            Thread.sleep(1000)
            client.send(TaskPayload(Member(1, "Member"), Operation.ADD_MEMBER, Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time))).toJson())
        })

        member.start()
        Thread.sleep(3000)
        assertEquals(serverController.members.size, initialSize + 1)
    }

    @Test
    fun taskAssignmentTest() {
        // member
        val member = Thread({
            val client = initClient()
            client.connect()
            Thread.sleep(1000)
            client.send(TaskPayload(Member(1, "Member"),
                Operation.ADD_MEMBER,
                Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time)))
                .toJson())
        })

        member.start()
        Thread.sleep(1000)

        // leader
        val leader = Thread({
            val client = initClient()
            client.connect()
            Thread.sleep(1000)
            client.send(TaskPayload(Member(1, "Member"),
                Operation.ADD_TASK,
                Task(1, "task dei cojoni", Status.RUNNING, Timestamp(Date().time), Timestamp(Date().time)))
                .toJson())
        })
        leader.start()
        Thread.sleep(5000)
    }
}