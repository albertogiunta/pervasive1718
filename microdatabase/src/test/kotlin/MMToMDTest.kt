import Connection.ADDRESS
import Connection.PORT_SEPARATOR
import Connection.PROTOCOL
import Connection.PROTOCOL_SEPARATOR
import Params.Log.TABLE_NAME
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import config.ConfigLoader
import config.Services
import junit.framework.Assert.assertTrue
import model.Log
import model.Serializer.klaxon
import model.SessionDNS
import org.junit.AfterClass
import org.junit.Test
import process.MicroServiceManager
import utils.handlingGetResponse

class MMToMDTest {

    companion object {
        private val startArguments = arrayOf("0")
        private val manager = MicroServiceManager()
        private val newSession: String
        private val closeSession: String
        private val getAllLogs: String
        private lateinit var session: SessionDNS

        private var logList = listOf<Log>()


        init {
            ConfigLoader("../config.json").load(startArguments)
            newSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/new/gntlrt94b21g479u"
            closeSession = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.SESSION.port}/session/close/"
            getAllLogs = "$PROTOCOL$PROTOCOL_SEPARATOR$ADDRESS$PORT_SEPARATOR${Services.DATA_BASE.port}/${Connection.API}/$TABLE_NAME/all"

            println()
            println("istanzio session")
            manager.newService(Services.SESSION, startArguments[0]) // 8500
            Thread.sleep(3000)
            println()
            println("istanzio monitor")
            manager.newService(Services.MONITOR, startArguments[0]) // 8200
            Thread.sleep(3000)
            println()
            println("istanzio database")
            manager.newService(Services.DATA_BASE, startArguments[0]) // 8100
            Thread.sleep(3000)

        }

        @AfterClass
        @JvmStatic
        fun closeConnection() {
            manager.closeSession(startArguments[0])
        }
    }

    @Test
    fun `add new session and start listening to monitor and write data`() {
        logList = handlingGetResponse(getAllLogs.httpGet().responseString())
        val startingSize = logList.size

        newSession.httpPost().responseString().third.fold(success = { session = klaxon.parse<SessionDNS>(it)!!; println("ho ricevuto risposta dal db: $session") }, failure = { println("ho ricevuto un errore $it") })
        Thread.sleep(10000)

        "$closeSession${session.sessionId}".httpDelete().responseString()

        logList = handlingGetResponse(getAllLogs.httpGet().responseString())
        val newSize = logList.size
        assertTrue(newSize > startingSize)

        Thread.sleep(2000)

        logList = handlingGetResponse(getAllLogs.httpGet().responseString())
        val newNewSize = logList.size
        assertTrue(newSize == newNewSize)
    }
}