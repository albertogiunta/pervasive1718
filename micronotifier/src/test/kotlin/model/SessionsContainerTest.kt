package model

import org.eclipse.jetty.websocket.api.*
import org.eclipse.jetty.websocket.api.Session
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.InetSocketAddress

class SessionsContainerTest {

    val sessionsContainer: SessionsContainer<Member, Session> = NotifierSessionsContainer()

    val m1 = Member("Mario Rossi")
    val m2 = Member("Padre Pio")

    @Before
    fun setUp() {}

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

        sessionsContainer.set(m1, ses)
        assertEquals(sessionsContainer[m1]!!, ses)
        assertEquals(sessionsContainer.getOn(ses), m1)
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

        sessionsContainer[m1] = ses
        assertEquals(sessionsContainer.removeListener(m1)!!, ses)
        assertNull(sessionsContainer.removeListener(m2))
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

        sessionsContainer[m1] = ses
        val member = sessionsContainer.removeListenerOn(ses)
        assertEquals(member, m1)
        assertNotEquals(member, m2)
    }

}