package integration

import WSClient
import config.ConfigLoader
import config.Services
import model.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import process.MicroServiceManager
import utils.GsonInitializer
import utils.toJson
import java.net.URI

class MMtoMNtoClientTest {

    private val sm = MicroServiceManager()

    val member = Member("MRIRSS70A00A000A")
    val closeMsg = PayloadWrapper(0, WSOperations.CLOSE, member.toJson())

    val subscription = Subscription(member, listOf(
            LifeParameters.DIASTOLIC_BLOOD_PRESSURE,
            LifeParameters.END_TIDAL_CARBON_DIOXIDE
    ))
    val subscriptionWrapper = PayloadWrapper(
            Services.instanceId(),
            WSOperations.SUBSCRIBE,
            subscription.toJson()
    )

    @Before
    fun onLoad() {

        ConfigLoader().load(listOf("0").toTypedArray())

        sm.newService(Services.NOTIFIER, "0", true).also { Thread.sleep(5000L) }
        sm.newService(Services.MONITOR, "0", true).also { Thread.sleep(5000L) }
        sm.newService(Services.DATA_BASE, "0", true).also { Thread.sleep(5000L) }
    }

    @Test
    fun testSubscription() {
        val checkBuffer = mutableListOf<String>()

        val client = WSClientForTest(Services.NOTIFIER.wsURI(), checkBuffer)

        client.connectBlocking()

        client.sendMessage(subscriptionWrapper.toJson())
        Thread.sleep(5000L)

        client.sendMessage(closeMsg.toJson())
        client.closeBlocking()

        if (checkBuffer.isEmpty()) {
            assertTrue(false)
        } else {
            assertTrue({
                val wrapper = GsonInitializer.fromJson(checkBuffer.first(), PayloadWrapper::class.java)
                when(wrapper.subject) {
                    WSOperations.ANSWER -> {
                        val res : Response = wrapper.objectify(wrapper.body)
                        res.code == 200 && res.toMessage == subscriptionWrapper.toJson()
                    }
                    else -> {
                        false
                    }
                }
            }.invoke())
        }
    }

    @Test
    fun testUpdates() {
        val checkBuffer = mutableListOf<String>()

        val client = WSClientForTest(Services.NOTIFIER.wsURI(), checkBuffer)

        client.connectBlocking()

        client.sendMessage(subscriptionWrapper.toJson())
        Thread.sleep(5000L)

        client.sendMessage(closeMsg.toJson())
        client.closeBlocking()

        if (checkBuffer.isEmpty()) {
            assertTrue(false)
        } else {
            checkBuffer.forEach {
                assertTrue({
                    val wrapper = GsonInitializer.fromJson(it, PayloadWrapper::class.java)
                    when(wrapper.subject) {
                        WSOperations.UPDATE -> {
                            val res : Update = wrapper.objectify(wrapper.body)
                            subscription.topics.contains(res.lifeParameter)
                        }
                        WSOperations.ANSWER -> {
                            true
                        }
                        WSOperations.NOTIFY -> {
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }.invoke())
            }
        }
    }

    @Test
    fun testNotification() {
        val checkBuffer = mutableListOf<String>()

        val client = WSClientForTest(Services.NOTIFIER.wsURI(), checkBuffer)

        client.connectBlocking()
        client.sendMessage(subscriptionWrapper.toJson())
        Thread.sleep(10000L)

        client.sendMessage(closeMsg.toJson())
        client.closeBlocking()

        if (checkBuffer.isEmpty()) {
            assertTrue(false)
        } else {
            checkBuffer.forEach {
                assertTrue({
                    val wrapper = GsonInitializer.fromJson(it, PayloadWrapper::class.java)
                    when(wrapper.subject) {
                        WSOperations.UPDATE -> {
                            true
                        }
                        WSOperations.ANSWER -> {
                            true
                        }
                        WSOperations.NOTIFY -> {
                            val res : Notification = wrapper.objectify(wrapper.body)
                            subscription.topics.contains(res.lifeParameter)
                        }
                        else -> {
                            false
                        }
                    }
                }.invoke())
            }
        }
    }

    @Test
    fun testClose() {
        val checkBuffer = mutableListOf<String>()

        val client = WSClientForTest(Services.NOTIFIER.wsURI(), checkBuffer)

        client.connectBlocking()
        client.sendMessage(subscriptionWrapper.toJson())
        Thread.sleep(2000L)
        client.sendMessage(closeMsg.toJson())
        Thread.sleep(2000L)

        if (checkBuffer.isEmpty()) {
            assertTrue(false)
        } else {
            assertTrue({
                val wrapper = GsonInitializer.fromJson(checkBuffer.last(), PayloadWrapper::class.java)
                when(wrapper.subject) {
                    WSOperations.ANSWER -> {
                        val res : Response = wrapper.objectify(wrapper.body)
                        res.code == 200 && res.toMessage == closeMsg.toJson()
                    }
                    else -> {
                        false
                    }
                }
            }.invoke())
        }

        client.closeBlocking()
    }

    @After
    fun onClose() {
        sm.closeSession("0")
    }

    class WSClientForTest(serverURI: URI, val buffer: MutableList<String>) : WSClient(serverURI) {

        override fun onMessage(message: String) {
            super.onMessage(message)
            buffer.add(message)
        }
    }
}