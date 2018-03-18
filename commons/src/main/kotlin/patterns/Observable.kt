package patterns

interface Observable {

    fun addObserver(observer: Observer)

    fun removeObserver(observer: Observer)

}

interface Observer {
    fun notify(obj : Any)
}