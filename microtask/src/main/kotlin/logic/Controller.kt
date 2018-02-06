package logic

import logic.ontologies.Status

/**
 * The interface representing the contract of the microservice
 */
interface Controller{

    fun addMember(member: Member)

    fun removeMember(member: Member)

    fun addTask(task:Task,member:Member)

    fun removeTask(task:Task)

    fun changeTaskStatus(task:Task,newStatus:Status)

    companion object {
        fun create() = ControllerImpl()
    }

}

class ControllerImpl:Controller{
    private val associationList: MutableList<Association> = mutableListOf()
    private val memberList:MutableList<Member> = mutableListOf()

    override fun addMember(member: Member){
        memberList + member
    }

    override fun removeMember(member: Member){
        memberList - member
    }

    override fun addTask(task:Task,member:Member){
        if(memberList.contains(member)){
            associationList + Association.create(task,member)
        }
    }

    override fun removeTask(task:Task){
        associationList.remove(associationList.first{it.task == task})
    }

    override fun changeTaskStatus(task:Task,newStatus:Status){
        associationList.first{it.task == task}.task.status = newStatus
    }

}