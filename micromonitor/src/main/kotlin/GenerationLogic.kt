import java.util.*

/**
 * Interface for a simulated data generation strategy
 * */
interface GenerationLogic<T> {
    fun nextValue(): T
}

/**
 * For performance reason the sin values are calculated one time at the program begin
 * and cached
 */
object ChachedSin {
    private val rad = 360

    var sin = Array<Double>(rad, { i ->
        Math.sin(Math.toRadians(i.toDouble()))
    })

    fun getValue(index: Int) = sin[index % rad]

    fun getRandomValue(): Double {
        var randomIndex = Math.abs(Random().nextInt()) % rad
        return sin[randomIndex]
    }
}

object GenerationStrategies {

    class DoubleSinusoidGeneration(val minBound: Double, val maxBound: Double) : GenerationLogic<Double> {

        private val range = maxBound - minBound

        override fun nextValue(): Double {
            return ((ChachedSin.getRandomValue() + 1.0) * range) + minBound
        }
    }

    class IntSinusoidGeneration(val minBound: Int, val maxBound: Int) : GenerationLogic<Int> {
        private val rad = 360
        private val range = maxBound - minBound

        override fun nextValue(): Int {
            return ((ChachedSin.getRandomValue() + 1).toInt() * range) + minBound
        }
    }

    class DoubleLinearGeneration(val minBound: Double, val maxBound: Double) : GenerationLogic<Double> {
        private var increase = true
        private var counter: Double = minBound
        override fun nextValue(): Double {
            if (increase) {
                ++counter
                if (counter == maxBound) increase = false
            } else {
                --counter
                if (counter == minBound) increase = true
            }
            return counter
        }

    }

}
