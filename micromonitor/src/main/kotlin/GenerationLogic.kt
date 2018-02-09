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
 * */
object CachedSin {
    private const val rad = 360

    /**
     * Array of precomputed sin values.
     * */
    val sin = Array(rad, { i ->
        Math.round(Math.sin(Math.toRadians(i.toDouble())) * 10000.0) / 10000.0
    })

    /**
     * Retrieves the sin of a given angle in degrees.
     *
     * @param index degree of the angle
     * */
    fun getValue(index: Int) = sin[index % rad]

    /**
     * Retrieves the sin of a random angle.
     * */
    fun getRandomValue(): Double {
        val randomIndex = Math.abs(Random().nextInt()) % rad
        return sin[randomIndex]
    }
}


object GenerationStrategies {

    /**
     * Defines a generator of double, working with sinusoid.
     * */
    class DoubleSinusoidGeneration(private val minBound: Double, private val maxBound: Double) : GenerationLogic<Double> {

        init {
            if (minBound >= maxBound) throw IllegalArgumentException("The minBound value must be less than maxBound")
        }
        private val range = maxBound - minBound

        /**
         * Retrieves a random double value in range.
         * */
        override fun nextValue(): Double {
            return Math.round((((CachedSin.getRandomValue() + 1.0) * range / 2) + minBound) * 100.0) /100.0
        }
    }

    /**
     * Defines a generator of integers, working with sinusoid.
     * */
    class IntSinusoidGeneration(private val minBound: Int, private val maxBound: Int) : GenerationLogic<Int> {

        init {
            if (minBound >= maxBound) throw IllegalArgumentException("The minBound value must be less than maxBound")
        }
        private val range = maxBound - minBound

        /**
         * Retrieves a random integer value in range.
         * */
        override fun nextValue(): Int {
            return (((CachedSin.getRandomValue() + 1.0) * range / 2) + minBound).toInt()
        }
    }

    /**
     * Defines a generator of double, increasing and decreasing values linearly.
     * */
    class DoubleLinearGeneration(private val minBound: Double, private val maxBound: Double) : GenerationLogic<Double> {
        private var increase = true
        private var counter: Double = minBound

        init {
            if (minBound >= maxBound) throw IllegalArgumentException("The minBound value must be less than maxBound")
        }

        /**
         * Retrieves a the next double value in range.
         * */
        override fun nextValue(): Double {
            if ((counter == maxBound && increase) || (counter == minBound && !increase)) increase = !increase
            if (increase) ++counter else --counter
            return counter
        }

        fun isIncreasing(): Boolean = increase
    }

    /**
     * Defines a generator of integers, increasing and decreasing values linearly.
     * */
    class IntLinearGeneration(private val minBound: Int, private val maxBound: Int) : GenerationLogic<Int> {
        private var increase = true
        private var counter: Int = minBound

        init {
            if (minBound >= maxBound) throw IllegalArgumentException("The minBound value must be less than maxBound")
        }

        /**
         * Retrieves a the next integer value in range.
         * */
        override fun nextValue(): Int {
            if ((counter == maxBound && increase) || (counter == minBound && !increase)) increase = !increase
            if (increase) ++counter else --counter
            return counter
        }

        fun isIncreasing(): Boolean = increase
    }
}