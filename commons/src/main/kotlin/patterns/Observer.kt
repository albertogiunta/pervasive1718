package patterns

interface Observable {

    fun addObserver(observer: Observer)

    fun removeObserver(observer: Observer)

    fun notify(obj: Any)
}

interface Observer {
    fun update(obj : Any)
}