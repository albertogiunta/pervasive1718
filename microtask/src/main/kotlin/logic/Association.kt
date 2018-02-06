package logic

interface Association{
    val task: Task
    val member: Member
    companion object {
        fun create(task:Task,member: Member) = AssociationImpl(task,member)
    }
}

data class AssociationImpl(override val task:Task, override val member: Member): Association