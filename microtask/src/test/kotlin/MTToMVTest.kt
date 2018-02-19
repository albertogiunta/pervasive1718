import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import config.ConfigLoader
import config.Services
import model.SessionDNS
import model.VisibleTask
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import process.MicroServiceManager
import utils.KlaxonDate
import utils.dateConverter
import utils.handlingGetResponse
import utils.mockLeaderMemberInteractionAndTaskAddition

class MTtoMVTest {

    private lateinit var listResult: List<VisibleTask>

    companion object {
        private val startArguments = arrayOf("0")
        private val getAllTaskVisor: String
        private val newSession: String
        private val manager = MicroServiceManager()
        private val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        private lateinit var session: SessionDNS

        init {
            ConfigLoader().load(startArguments)
            getAllTaskVisor = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.VISORS.port}/${Connection.API}/all"
            newSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/new/gntlrt94b21g479u"

            println()
            println("istanzio monitor")
            manager.newService(Services.MONITOR, startArguments[0]) // 8200
            Thread.sleep(3000)
            println()
            println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)
            println()
            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            println()
            println("istanzio visors")
            manager.newService(Services.VISORS, startArguments[0]) // 8400
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
    fun `create leader and member interaction and add task`() {
        val taskId = 32

        mockLeaderMemberInteractionAndTaskAddition(session, 4, taskId, removeTask = false)

        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString())

        Assert.assertTrue(listResult.firstOrNull { it.id == taskId } != null)

    }

    @Test
    fun `create leader and member interaction, add task and remove task`() {
        val taskId = 35

        mockLeaderMemberInteractionAndTaskAddition(session, 5, 35, removeTask = true)

        listResult = handlingGetResponse(getAllTaskVisor.httpGet().responseString())

        Assert.assertTrue(listResult.firstOrNull { it.id == taskId } == null)
    }
}