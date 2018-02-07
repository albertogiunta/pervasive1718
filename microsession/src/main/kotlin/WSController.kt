import java.util.concurrent.atomic.AtomicBoolean

class WSController(ws: WSTaskServer) {

    companion object {
        lateinit var INSTANCE: WSController
        private val isInitialized = AtomicBoolean()

        fun init(ws: WSTaskServer) {
            if (!isInitialized.getAndSet(true)) {
                INSTANCE = WSController(ws)
            }
        }
    }

}