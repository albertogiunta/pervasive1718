import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import config.ConfigLoader
import config.Services
import model.Member
import model.SessionDNS
import model.Status
import model.Task
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import process.MicroServiceManager
import utils.*
import java.sql.Timestamp
import java.util.*

class MTtoDBTest {

    private lateinit var listResult:List<model.Task>

    companion object {
        private val startArguments = arrayOf("0")
        private lateinit var readTask: String
        private lateinit var newSession: String
        private val manager = MicroServiceManager()
        private val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        private lateinit var session: SessionDNS

        @BeforeClass
        @JvmStatic
        fun getSession() {
            ConfigLoader().load(startArguments)
            readTask = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/task/all"
            newSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/new/gntlrt94b21g479u"

            println()
            println("istanzio monitor")
            manager.newService(Services.MONITOR, startArguments[0]) // 8600
            Thread.sleep(3000)
            println()
            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            println()
            println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)
            println()
            println("istanzio task")
            manager.newService(Services.TASK_HANDLER, startArguments[0]) // 8200
            Thread.sleep(3000)

            newSession.httpPost().responseString().third.fold(success = { session = klaxon.parse<SessionDNS>(it)!!; println("ho ricevuto risposta dal db: $session") }, failure = { println("ho ricevuto un errore $it") })
        }

        @AfterClass
        @JvmStatic
        fun destroyAll() {
            manager.closeSession(startArguments[0])
        }
    }

    @Test
    fun addTask(){
//        Thread.sleep(4000)
//        addLeaderThread(memberId = -1).start()
//        Thread.sleep(4000)
//
//        val member = Member(4,"Member")
//        addMemberThread(memberId = member.id).start()
//        Thread.sleep(3000)
//
//        val task = Task(-1, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)
//
//        addTaskThread(task, member).start()
//        Thread.sleep(4000)
//
//        listResult = handlingGetResponse(readTask.httpGet().responseString())
//        Thread.sleep(2000)
//        println(listResult)

        val taskId = 32

        mockLeaderMemberInteractionAndTaskAddition(session, 4, taskId, removeTask = false)

        listResult = handlingGetResponse(readTask.httpGet().responseString())

        assertTrue(listResult.firstOrNull { it.id == taskId } != null)
    }

    @Test
    fun removeTask(){
        Thread.sleep(5000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member(5,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = Task(41, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(3000)

        removeTaskThread(task).start()
        Thread.sleep(3000)

        listResult = handlingGetResponse(readTask.httpGet().responseString())
        Thread.sleep(1000)
        println(listResult)

        assertTrue(listResult.firstOrNull { it.id == task.id } == null)
    }

    @Test
    fun changeTaskStatus(){
        Thread.sleep(5000)
        addLeaderThread(memberId = -1).start()
        Thread.sleep(3000)

        val member = Member(3,"Member")
        addMemberThread(memberId = member.id).start()
        Thread.sleep(3000)

        val task = Task(42, session.sessionId, member.id, Timestamp(Date().time), Timestamp(Date().time+1000),1, Status.RUNNING.id)

        addTaskThread(task, member).start()
        Thread.sleep(3000)

        task.statusId = Status.FINISHED.id
        changeTaskStatus(task).start()
        Thread.sleep(3000)

        listResult = handlingGetResponse(readTask.httpGet().responseString())
        Thread.sleep(4000)
        assertTrue(listResult.firstOrNull{it.id == task.id}!!.statusId == Status.FINISHED.id)
    }
}