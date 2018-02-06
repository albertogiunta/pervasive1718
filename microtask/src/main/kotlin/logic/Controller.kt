package logic

/**
 * The interface representing the contract of the microservice
 */
interface Controller{

    fun addTask(Task task, Member member):Unit

    fun removeTask(Task task):Unit

    fun changeTaskStatus(Task task, Status newStatus):Unit

}