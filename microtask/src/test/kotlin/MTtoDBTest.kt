import logic.TaskController
import networking.WSTaskServer
import org.junit.Test
import spark.kotlin.ignite

class MTtoDBTest {

    companion object {
        private var taskController: TaskController

        init {
            val taskService = ignite()
            taskService.port(DefaultPorts.taskPort)
            taskService.service.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            taskService.service.init()
            Thread.sleep(1000)

            taskController = TaskController.INSTANCE

            MicroSessionBootstrap.init(DefaultPorts.sessionPort)

        }
    }

    @Test
    fun addTask(){

    }

    @Test
    fun removeTask(){

    }

    @Test
    fun changeTaskStatus(){

    }

}
