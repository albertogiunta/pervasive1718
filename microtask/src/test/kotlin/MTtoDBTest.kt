import Connection.ADDRESS
import Connection.DB_PORT
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
import logic.Member
import logic.Status
import config.Services
import logic.TaskController
import networking.WSTaskServer
import org.junit.Assert.assertTrue
import org.junit.Test
import spark.kotlin.ignite
import java.io.StringReader
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class MTtoDBTest {

    private val readTask:String = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR$DB_PORT/${Connection.API}/task/all"
    private lateinit var listResult:ArrayList<model.Task>

    companion object {
        private var taskController: TaskController

        init {
            val taskService = ignite()
            taskService.port(Services.TASK_HANDLER.port)
            taskService.service.webSocket(Services.TASK_HANDLER.wsPath, WSTaskServer::class.java)
            taskService.service.init()
            Thread.sleep(3000)

            taskController = TaskController.INSTANCE

            //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microDatabase).killAtParentDeath()
            //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microSession).killAtParentDeath()
            Thread.sleep(5000)
            //MicroSessionBootstrap.init(Services.SESSION.port)da vedere come farli
	        //MicroDatabaseBootstrap.init(Connection.DB_PORT.toInt())
        }
    }


    @Test
    fun addTask(){
        //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microDatabase).killAtParentDeath()
        //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microSession).killAtParentDeath()
        Thread.sleep(5000)
        Thread.sleep(4000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(4000)

        val member = Member(4,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = logic.Task(24,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(4000)

        handlingGetResponse(readTask.httpGet().responseString())
        Thread.sleep(2000)
        println(listResult)

        assertTrue(listResult.firstOrNull { it.id == task.id } != null)

    }


    @Test
    fun removeTask(){
        //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microDatabase).killAtParentDeath()
        //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microSession).killAtParentDeath()
        Thread.sleep(5000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member(5,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = logic.Task(25,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(3000)

        removeTaskThread(task).start()
        Thread.sleep(3000)

        handlingGetResponse(readTask.httpGet().responseString())
        Thread.sleep(1000)
        println(listResult)

        assertTrue(listResult.firstOrNull { it.id == task.id } == null)
    }

    @Test
    fun changeTaskStatus(){
        //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microDatabase).killAtParentDeath()
        //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microSession).killAtParentDeath()
        Thread.sleep(5000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member(3,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = logic.Task(26,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(3000)

        task.statusId = Status.FINISHED.id
        changeTaskStatus(task).start()
        Thread.sleep(3000)

        handlingGetResponse(readTask.httpGet().responseString())
        Thread.sleep(4000)
        assertTrue(listResult.firstOrNull{it.id == task.id}!!.statusId == Status.FINISHED.id)
    }

    private fun handlingGetResponse(triplet: Triple<Request, Response, Result<String, FuelError>>) {
        triplet.third.fold(success = {
            val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
            JsonReader(StringReader(it)).use { reader ->
                listResult = arrayListOf()
                reader.beginArray {
                    while (reader.hasNext()) {
                        val task = klaxon.parse<model.Task>(reader)!!
                        (listResult).add(task)
                    }
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }

}
