import org.junit.Assert
import org.junit.Test

class GenerationStrategiesTest {

    @Test
    fun doubleSinusoidGenBoundaryCheck() {
        var minBound = 0.0
        var maxBound = 100.0
        var dSG = GenerationStrategies.DoubleSinusoidGeneration(minBound, maxBound)
        for (item in 0 until 360) {
            val generatedVal = dSG.nextValue()
            println("GeneratedVal " + generatedVal)
            Assert.assertTrue(generatedVal >= minBound)
            Assert.assertTrue(generatedVal <= maxBound)
        }
    }

    @Test
    fun intSinusoidGenBoundaryCheck() {
        var minBound = 0
        var maxBound = 100
        var iSG = GenerationStrategies.IntSinusoidGeneration(minBound, maxBound)
        for (item in 0 until 360) {
            val generatedVal = iSG.nextValue()
            println("GeneratedVal " + generatedVal)
            Assert.assertTrue(generatedVal >= minBound)
            Assert.assertTrue(generatedVal <= maxBound)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun doubleLinearGenerationException() {
        val minBound = 100.0
        val maxBound = 0.0
        GenerationStrategies.DoubleLinearGeneration(minBound, maxBound)
    }

    @Test
    fun doubleLinearGeneration() {
        var minBound = 0.0
        var maxBound = 100.0
        var dLG = GenerationStrategies.DoubleLinearGeneration(minBound, maxBound)
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

    @Test(expected = IllegalArgumentException::class)
    fun intLinearGenerationException() {
        val minBound = 100
        val maxBound = 0
        GenerationStrategies.IntLinearGeneration(minBound, maxBound)
    }

    @Test
    fun intLinearGeneration() {
        var minBound = 0
        var maxBound = 100
        var iLG = GenerationStrategies.IntLinearGeneration(minBound, maxBound)
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
}