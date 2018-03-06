package logic

import model.VisibleTask

class TaskManager {

    private val tasks: MutableList<VisibleTask> = ArrayList()

    fun addTask(task: VisibleTask) {
        tasks += task
    }

    fun removeTask(taskName: String) {
        tasks.remove(tasks.find { t -> t.taskName == taskName})
    }

    fun getAllTasks() : MutableList<VisibleTask> = tasks
}