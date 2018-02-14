import config.Services
import logic.TaskController
import networking.WSTaskServer
import org.junit.Test
import spark.kotlin.ignite

class MTtoDBTest {

    companion object {
        private var taskController: TaskController

        init {
            val taskService = ignite()
            taskService.port(Services.TASK_HANDLER.port)
            taskService.service.webSocket(Services.TASK_HANDLER.wsPath, WSTaskServer::class.java)
            taskService.service.init()
            Thread.sleep(1000)

            taskController = TaskController.INSTANCE

            MicroSessionBootstrap.init(Services.SESSION.port)

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
