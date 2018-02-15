import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import config.Services
import logic.Member
import logic.Status
import logic.TaskController
import logic.VisibleTask
import networking.WSTaskServer
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import process.MicroServiceManager
import spark.kotlin.ignite
import java.io.StringReader
import java.sql.Timestamp
import java.util.*

class MTtoMVTest {

    private val getAllTaskVisor: String = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.VISORS.port}/${Connection.API}/all"
    private lateinit var listResult: List<VisibleTask>

    companion object {
        private var taskController: TaskController
        private val manager = MicroServiceManager(System.getProperty("user.dir"))

        init {
            val taskService = ignite()
            taskService.port(Services.TASK_HANDLER.port)
            taskService.service.webSocket(Services.TASK_HANDLER.wsPath, WSTaskServer::class.java)
            taskService.service.init()
            Thread.sleep(3000)

            taskController = TaskController.INSTANCE


            manager.newService(Services.SESSION,"666")
            Thread.sleep(3000)
            manager.newService(Services.DATA_BASE,"666")
            Thread.sleep(3000)
            manager.newService(Services.VISORS,"666")
            Thread.sleep(3000)
        }

        @AfterClass
        @JvmStatic
        fun destroyAll() {
            manager.closeSession("666")
        }
    }

    @Test
    fun addTask(){
        Thread.sleep(4000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(4000)

        val member = Member(4,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = logic.Task(32,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(4000)

        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString())
        Thread.sleep(2000)
        println(listResult)

        Assert.assertTrue(listResult.firstOrNull { it.id == task.id } != null)

    }


    @Test
    fun removeTask(){
        Thread.sleep(5000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member(5,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = logic.Task(35,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(3000)

        removeTaskThread(task).start()
        Thread.sleep(3000)

        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString())
        Thread.sleep(1000)
        println(listResult)

        Assert.assertTrue(listResult.firstOrNull { it.id == task.id } == null)
    }


}