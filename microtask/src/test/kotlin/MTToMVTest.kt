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
import org.junit.Assert
import org.junit.Test
import spark.kotlin.ignite
import java.io.StringReader
import java.sql.Timestamp
import java.util.*

class MTtoMVTest {

    private val getAllTaskVisor: String = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.VISORS.port}/${Connection.API}/all"
    private lateinit var listResult: ArrayList<VisibleTask>

    companion object {
        private var taskController: TaskController

        init {
            val taskService = ignite()
            taskService.port(Services.TASK_HANDLER.port)
            taskService.service.webSocket(Services.TASK_HANDLER.wsPath, WSTaskServer::class.java)
            taskService.service.init()
            Thread.sleep(3000)

            taskController = TaskController.INSTANCE

            //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microVisors).killAtParentDeath()
            //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microDatabase).killAtParentDeath()
            //MicroserviceBootUtils.startMicroservice(MicroservicesPaths.microSession).killAtParentDeath()
            //MicroSessionBootstrap.init(Services.SESSION.port)da vedere come farli
            //MicroDatabaseBootstrap.init(Connection.DB_PORT.toInt())
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

        val task = logic.Task(28,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(4000)

        handlingGetResponse(getAllTaskVisor.httpGet().responseString())
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

        val task = logic.Task(29,member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(3000)

        removeTaskThread(task).start()
        Thread.sleep(3000)

        handlingGetResponse(getAllTaskVisor.httpGet().responseString())
        Thread.sleep(1000)
        println(listResult)

        Assert.assertTrue(listResult.firstOrNull { it.id == task.id } == null)
    }

    private fun handlingGetResponse(triplet: Triple<Request, Response, Result<String, FuelError>>) {
        triplet.third.fold(success = {
            val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
            JsonReader(StringReader(it)).use { reader ->
                listResult = arrayListOf()
                reader.beginArray {
                    while (reader.hasNext()) {
                        val task = klaxon.parse<VisibleTask>(reader)!!
                        (listResult).add(task)
                    }
                }
            }
        }, failure = {
            println(String(it.errorData))
        })
    }
}