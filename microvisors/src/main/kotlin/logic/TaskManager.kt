package logic

import model.VisibleTask

class TaskManager {

    private val tasks: MutableList<VisibleTask> = ArrayList()

    fun addTask(task: VisibleTask) {
        tasks += task
    }

    fun removeTask(taskId: Int) {
        tasks.remove(tasks.find { t -> t.id == taskId})
    }

    fun getAllTasks() : MutableList<VisibleTask> = tasks
}