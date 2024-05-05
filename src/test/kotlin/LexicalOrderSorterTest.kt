import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

class LexicalOrderSorterTest {
    private lateinit var sorter: LexicalOrderSorter

    private val processWordPair = getAccessibleMethod("processWordPair")
    private val buildAdjacencyList = getAccessibleMethod("buildAdjacencyList")
    private val topologicalSort = getAccessibleMethod("topologicalSort")

    @BeforeEach
    fun setUp() {
        sorter = LexicalOrderSorter()
    }

    private fun getAccessibleMethod(methodName: String) = LexicalOrderSorter::class.declaredMemberFunctions
        .firstOrNull { it.name == methodName }?.apply { isAccessible = true }
        ?: throw AssertionError("Method not found: $methodName")

    @Test
    fun testProcessWordPairDifferentFirstCharacters() {
        assertTrue(processWordPair.call(sorter, "apple", "banana") as Boolean,
            "Expected 'apple' to come before 'banana' in lexical order")
    }

    @Test
    fun testProcessWordPairSamePrefixDifferentLength() {
        assertTrue(processWordPair.call(sorter, "app", "apple") as Boolean,
            "Expected 'app' to come before 'apple' since 'app' is a prefix of 'apple'")
    }

    @Test
    fun testBuildAdjacencyListWithValidSequence() {
        val words = listOf("ax", "ab", "eb", "ex")
        assertTrue(buildAdjacencyList.call(sorter, words) as Boolean,
            "Expected adjacency list to be built successfully for valid sequence")
    }

    @Test
    fun testBuildAdjacencyListWithInvalidSequence() {
        val words = listOf("apple", "app")
        assertFalse(buildAdjacencyList.call(sorter, words) as Boolean,
            "Expected adjacency list to fail with 'apple' following 'app'")
    }

    @Test
    fun testTopologicalSortPossible() {
        buildAdjacencyList.call(sorter, listOf("ax", "az", "bx", "ba"))
        assertEquals("xabzcdefghijklmnopqrstuvwy", topologicalSort.call(sorter) as String?,
            "Expected topological sort to yield a valid character sequence")
    }

    @Test
    fun testTopologicalSortImpossible() {
        buildAdjacencyList.call(sorter, listOf("ax", "ab", "bx", "ba", "xa"))
        assertNull(topologicalSort.call(sorter),
            "Expected topological sort to be impossible due to cycle or other constraints")
    }
}
