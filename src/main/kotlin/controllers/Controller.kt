package controllers

interface Controller {

    companion object {
        val applicationJsonRequestType = "application/json"
    }

    fun initRoutes()

}