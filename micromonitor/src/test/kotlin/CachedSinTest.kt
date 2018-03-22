import org.junit.Assert
import org.junit.Test

class CachedSinTest {

    @Test
    fun `checks if sin defined function returns valid values`() {
        CachedSin.sin.forEach {
            println(it)
            Assert.assertTrue(it <= 1.0 && it >= -1.0)
        }
    }

    @Test
    fun `checks if sin defined function returns correct specified values`() {
        Assert.assertTrue(CachedSin.getValue(0) == 0.0)
        Assert.assertTrue(CachedSin.getValue(90) == 1.0)
        Assert.assertTrue(CachedSin.getValue(180) == 0.0)
        Assert.assertTrue(CachedSin.getValue(270) == -1.0)
    }

    @Test
    fun `checks if sin defined function returns valid values 2`() {
        val sin = CachedSin.sin
        val randomVal = CachedSin.getRandomValue()
        println("Random sin val " + randomVal)
        Assert.assertTrue(sin.contains(randomVal))
    }
}