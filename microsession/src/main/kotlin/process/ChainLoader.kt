package process

import java.util.*

class ChainLoader<T, R> {

    private val chain : Queue<(T) -> R> = ArrayDeque()
    private val arguments : Queue<T> = ArrayDeque()

    init {}

    fun add(args: T, code: (T) -> R) : ChainLoader<T, R> {
        chain.offer(code)
        arguments.offer(args)
        return this
    }

    fun consume() : Queue<R> {
        val chainLength = chain.size
        val results : Queue<R> = ArrayDeque()

        (0 until chainLength).forEach {
            if (chain.isNotEmpty()) {
                val args = arguments.poll()
                results.offer(chain.poll()(args))
            }
        }

        return results
    }
}

fun main(args: Array<String>) {

    val loader = ChainLoader<Unit, Unit>()
    var chained : Int = 0

    println("Building function stack")

    (0 until 10).forEach {
        loader.add (args = Unit, code = {
            Thread {
                println(chained ++)
            }.run()
        })
    }

    println("Executing function stack")

    loader.consume()

}