import org.junit.Assert
import org.junit.Test

class GenerationStrategiesTest {

    /*
     * Checks if the double sinusoid generator returns values in boundaries.
     */
    @Test
    fun doubleSinusoidGenBoundaryCheck() {
        val minBound = 0.0
        val maxBound = 100.0
        val dSG = GenerationStrategies.DoubleSinusoidGeneration(minBound, maxBound)
        for (item in 0 until 360) {
            val generatedVal = dSG.nextValue()
            println("GeneratedVal " + generatedVal)
            Assert.assertTrue(generatedVal >= minBound)
            Assert.assertTrue(generatedVal <= maxBound)
        }
    }

    /*
     * Checks if the int sinusoid generator returns values in boundaries.
     */
    @Test
    fun intSinusoidGenBoundaryCheck() {
        val minBound = 0
        val maxBound = 100
        val iSG = GenerationStrategies.IntSinusoidGeneration(minBound, maxBound)
        for (item in 0 until 360) {
            val generatedVal = iSG.nextValue()
            println("GeneratedVal " + generatedVal)
            Assert.assertTrue(generatedVal >= minBound)
            Assert.assertTrue(generatedVal <= maxBound)
        }
    }

    /*
     * Checks if the double linear generator returns expected values and in boundaries.
     */
    @Test
    fun doubleLinearGeneration() {
        val minBound = 0.0
        val maxBound = 100.0
        val dLG = GenerationStrategies.DoubleLinearGeneration(minBound, maxBound)
        var prevVal = minBound

        for (item in 0 until 360) {
            val generatedVal = dLG.nextValue()
            println("GeneratedVal " + generatedVal)
            if (dLG.isIncreasing()) {
                Assert.assertTrue(prevVal + 1 == generatedVal)
            } else {
                Assert.assertTrue(prevVal - 1 == generatedVal)
            }
            Assert.assertTrue(generatedVal >= minBound)
            Assert.assertTrue(generatedVal <= maxBound)
            prevVal = generatedVal
        }
    }

    /*
     * Checks if the integer linear generator returns expected values and in boundaries.
     */
    @Test
    fun intLinearGeneration() {
        val minBound = 0
        val maxBound = 100
        val iLG = GenerationStrategies.IntLinearGeneration(minBound, maxBound)
        var prevVal = minBound

        for (item in 0 until 360) {
            val generatedVal = iLG.nextValue()
            println("GeneratedVal " + generatedVal)
            if (iLG.isIncreasing()) {
                Assert.assertTrue(prevVal + 1 == generatedVal)
            } else {
                Assert.assertTrue(prevVal - 1 == generatedVal)
            }
            Assert.assertTrue(generatedVal >= minBound)
            Assert.assertTrue(generatedVal <= maxBound)
            prevVal = generatedVal
        }
    }

    /*
     * Checks if IllegalArgumentException is thrown in DoubleSinusoidGeneration
     */
    @Test(expected = IllegalArgumentException::class)
    fun doubleSinusoidGenerationException() {
        val minBound = 100.0
        val maxBound = 0.0
        GenerationStrategies.DoubleSinusoidGeneration(minBound, maxBound)
    }

    /*
     * Checks if IllegalArgumentException is thrown in IntSinusoidGeneration
     */
    @Test(expected = IllegalArgumentException::class)
    fun intSinusoidGenerationException() {
        val minBound = 100
        val maxBound = 0
        GenerationStrategies.IntSinusoidGeneration(minBound, maxBound)
    }

    /*
     * Checks if IllegalArgumentException is thrown in DoubleLinearGeneration
     */
    @Test(expected = IllegalArgumentException::class)
    fun doubleLinearGenerationException() {
        val minBound = 100.0
        val maxBound = 0.0
        GenerationStrategies.DoubleLinearGeneration(minBound, maxBound)
    }

    /*
     * Checks if IllegalArgumentException is thrown in IntLinearGeneration
     */
    @Test(expected = IllegalArgumentException::class)
    fun intLinearGenerationException() {
        val minBound = 100
        val maxBound = 0
        GenerationStrategies.IntLinearGeneration(minBound, maxBound)
    }
}