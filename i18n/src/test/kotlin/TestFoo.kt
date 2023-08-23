import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestFoo {

    @Test
    fun test() {
        val foo = Foo()
        assertEquals("Hello", foo.hello())
    }
}
