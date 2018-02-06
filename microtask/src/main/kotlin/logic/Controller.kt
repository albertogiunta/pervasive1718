package logic

import logic.ontologies.Status

/**
 * The interface representing the contract of the microservice
 */
interface Controller{

    val associationList: MutableList<Association>

    fun addTask(task:Task,member:Member):Unit{
        associationList + Association.create(task,member)
    }

    fun removeTask(task:Task):Unit{
        associationList.remove(associationList.first{it.task == task})
    }

    fun changeTaskStatus(task:Task,newStatus:Status):Unit{
        associationList.first{it.task == task}.task.status = newStatus
    }

    companion object {
        fun create(associationList: MutableList<Association>) = ControllerImpl(associationList)
    }

}

class ControllerImpl(override val associationList:MutableList<Association>):Controller{

}