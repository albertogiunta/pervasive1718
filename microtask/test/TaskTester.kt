import src.main.kotlin.microtask.ToBeTestedObj
import org.junit.Assert
import org.junit.Test

class RolesEndpointsTest {

    @Test
    fun sumTest() {
        Assert.assertEquals(7, ToBeTestedObj.sum(3,4))
        Assert.assertNotEquals(20, ToBeTestedObj.sum(3,4))
    }

}