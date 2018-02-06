import java.util.*

/**
 * Interface for a simulated data generation strategy
 * */
interface GenerationLogic<T> {
    fun nextValue(): T
}

object GenerationStrategies {

    class SinusoidGeneration : GenerationLogic<Double> {
        override fun nextValue(): Double {
            return (Math.sin(Random().nextDouble() % 60) + 1) * 30
        }

    }

    class LinearGeneration : GenerationLogic<Double> {
        var counter: Double = 0.0
        override fun nextValue(): Double {
            return ++counter
        }

    }

}
