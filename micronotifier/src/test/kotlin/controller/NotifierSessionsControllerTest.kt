package controller

import model.Member
import org.eclipse.jetty.websocket.api.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.InetSocketAddress


class NotifierSessionsControllerTest {

    lateinit var sesCon: SessionsController<Member, Session>

    val m1 = Member("Mario Rossi")
    val m2 = Member("Padre Pio")

    @Before
    fun setUp() {
        sesCon = NotifierSessionsController()
    }

    @Test
    fun setAndGet() {
        val ses = object : Session {
            override fun getRemote(): RemoteEndpoint {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getLocalAddress(): InetSocketAddress {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun disconnect() {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getProtocolVersion(): String {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getUpgradeResponse(): UpgradeResponse {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun setIdleTimeout(ms: Long) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getPolicy(): WebSocketPolicy {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getUpgradeRequest(): UpgradeRequest {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun suspend(): SuspendToken {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun isOpen(): Boolean {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getIdleTimeout(): Long {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close() {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close(closeStatus: CloseStatus?) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close(statusCode: Int, reason: String?) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun isSecure(): Boolean {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getRemoteAddress(): InetSocketAddress {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }
        }

        sesCon.set(m1, ses)
        assertEquals(sesCon[m1]!!, ses)
        assertEquals(sesCon.getOn(ses), m1)
    }

    @Test
    fun removeListener() {
        val ses = object : Session {
            override fun getRemote(): RemoteEndpoint {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getLocalAddress(): InetSocketAddress {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun disconnect() {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getProtocolVersion(): String {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getUpgradeResponse(): UpgradeResponse {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun setIdleTimeout(ms: Long) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getPolicy(): WebSocketPolicy {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getUpgradeRequest(): UpgradeRequest {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun suspend(): SuspendToken {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun isOpen(): Boolean {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getIdleTimeout(): Long {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close() {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close(closeStatus: CloseStatus?) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close(statusCode: Int, reason: String?) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun isSecure(): Boolean {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getRemoteAddress(): InetSocketAddress {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }
        }

        sesCon[m1] = ses
        assertEquals(sesCon.removeListener(m1)!!, ses)
        assertNull(sesCon.removeListener(m2))
    }

    @Test
    fun removeListenerOn() {
        val ses = object : Session {
            override fun getRemote(): RemoteEndpoint {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getLocalAddress(): InetSocketAddress {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun disconnect() {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getProtocolVersion(): String {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getUpgradeResponse(): UpgradeResponse {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun setIdleTimeout(ms: Long) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getPolicy(): WebSocketPolicy {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getUpgradeRequest(): UpgradeRequest {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun suspend(): SuspendToken {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun isOpen(): Boolean {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getIdleTimeout(): Long {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close() {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close(closeStatus: CloseStatus?) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun close(statusCode: Int, reason: String?) {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun isSecure(): Boolean {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }

            override fun getRemoteAddress(): InetSocketAddress {
                TODO("not implemented") //To change boundaries of created functions use File | Settings | File Templates.
            }
        }

        sesCon[m1] = ses
        val iter = sesCon.removeListenerOn(ses)
        iter.forEach({ println(it) })
        assertTrue(iter.contains(m1))
        assertFalse(iter.contains(m2))
    }

}