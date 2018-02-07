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
object CachedSin {
    private val rad = 360

    val sin = Array<Double>(rad, { i ->
        Math.round(Math.sin(Math.toRadians(i.toDouble())) * 10000.0) / 10000.0
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
            return ((CachedSin.getRandomValue() + 1.0) * range / 2) + minBound
        }
    }

    class IntSinusoidGeneration(val minBound: Int, val maxBound: Int) : GenerationLogic<Int> {
        private val rad = 360
        private val range = maxBound - minBound

        override fun nextValue(): Int {
            return (((CachedSin.getRandomValue() + 1.0) * range / 2) + minBound).toInt()
        }
    }

    class DoubleLinearGeneration(val minBound: Double, val maxBound: Double) : GenerationLogic<Double> {
        private var increase = true
        private var counter: Double = minBound

        init {
            if (minBound >= maxBound) throw IllegalArgumentException("The minBound value must be less than maxBound")
        }

        override fun nextValue(): Double {
            if ((counter == maxBound && increase) || (counter == minBound && !increase)) increase = !increase
            if (increase) {
                ++counter
            } else {
                --counter
            }
            return counter
        }

        fun isIncreasing(): Boolean = increase
    }

    class IntLinearGeneration(val minBound: Int, val maxBound: Int) : GenerationLogic<Int> {
        private var increase = true
        private var counter: Int = minBound

        init {
            if (minBound >= maxBound) throw IllegalArgumentException("The minBound value must be less than maxBound")
        }

        override fun nextValue(): Int {
            if ((counter == maxBound && increase) || (counter == minBound && !increase)) increase = !increase
            if (increase) {
                ++counter
            } else {
                --counter
            }
            return counter
        }

        fun isIncreasing(): Boolean = increase
    }

}
