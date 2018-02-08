package logic

/**
 * The interface representing the contract of the microservice
 */
interface Controller {

    val taskMemberAssociationList: MutableList<TaskMemberAssociation>

    fun addTask(task: Task, member: Member)

    fun removeTask(task: Task)

    fun changeTaskStatus(task: Task)

}