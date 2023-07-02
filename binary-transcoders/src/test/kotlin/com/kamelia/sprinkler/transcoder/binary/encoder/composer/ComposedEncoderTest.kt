package com.kamelia.sprinkler.transcoder.binary.encoder.composer

import com.kamelia.sprinkler.transcoder.binary.encoder.BooleanEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.ByteEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.DoubleEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.FloatEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.IntEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.LongEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.ShortEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.UTF8StringEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.EncoderOutput
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ComposedEncoderTest {

    class BasicTypes(
        val byte: Byte,
        val short: Short,
        val int: Int,
        val long: Long,
        val float: Float,
        val double: Double,
        val boolean: Boolean,
        val string: String,
    )

    class Node(val value: Byte, val next: Node? = null)

    class TreeNode(val value: Byte, val left: TreeNode? = null, val right: TreeNode? = null)

    class RecurseWithArray(val value: Byte, val array: Array<RecurseWithArray>)

    class RecurseWithCollection(val value: Byte, val collection: Collection<RecurseWithCollection>)

    sealed interface CustomNode {

        class Leaf(val value: Byte) : CustomNode

        class Branch(val left: CustomNode, val right: CustomNode) : CustomNode

    }

    @Test
    fun `basic composed encoder works correctly`() {
        val numbers = BasicTypes(1, 2, 3, 4, 5.0f, 6.0, false, "Hello World!")

        val byte = ByteEncoder()
        val short = ShortEncoder()
        val int = IntEncoder()
        val long = LongEncoder()
        val float = FloatEncoder()
        val double = DoubleEncoder()
        val boolean = BooleanEncoder()
        val string = UTF8StringEncoder()

        val baseEncoder = Encoder<BasicTypes> { obj, output ->
            byte.encode(obj.byte, output)
            short.encode(obj.short, output)
            int.encode(obj.int, output)
            long.encode(obj.long, output)
            float.encode(obj.float, output)
            double.encode(obj.double, output)
            boolean.encode(obj.boolean, output)
            string.encode(obj.string, output)
        }

        val composedEncoder = composedEncoder<BasicTypes> {
            encode(it.byte)
            encode(it.short)
            encode(it.int)
            encode(it.long)
            encode(it.float)
            encode(it.double)
            encode(it.boolean)
            encode(it.string)
        }

        assertArrayEquals(baseEncoder.encode(numbers), composedEncoder.encode(numbers))
    }

    @Test
    fun `composed encoder works with simple recursive objects`() {
        val node = Node(1, Node(2, Node(3)))

        val encoder = composedEncoder<Node> {
            encode(it.value)
            encode(it.next)
        }

        val array = encoder.encode(node)

        val expected = byteArrayOf(1, 1, 2, 1, 3, 0)
        assertArrayEquals(expected, array)
    }

    @Test
    fun `composed encoder works with complex recursive objects`() {
        val node = TreeNode(
            1,
            TreeNode(2, TreeNode(3), TreeNode(4)),
            TreeNode(5, TreeNode(6), TreeNode(7))
        )

        val encoder = composedEncoder<TreeNode> {
            encode(it.value)
            encode(it.left)
            encode(it.right)
        }

        val array = encoder.encode(node)

        val expected = byteArrayOf(
            1,
            1, 2,
            1, 3, 0, 0,
            1, 4, 0, 0,
            1, 5,
            1, 6, 0, 0,
            1, 7, 0, 0
        )

        assertArrayEquals(expected, array)
    }

    @Test
    fun `encoding of recursive objects with array works`() {
        val node = RecurseWithArray(
            1,
            arrayOf(
                RecurseWithArray(2, arrayOf()),
                RecurseWithArray(3, arrayOf()),
            )
        )

        val encoder = composedEncoder<RecurseWithArray> {
            encode(it.value)
            encode(it.array)
        }

        val array = encoder.encode(node)

        val expected = byteArrayOf(
            1, 0, 0, 0, 2,
            2, 0, 0, 0, 0,
            3, 0, 0, 0, 0,
        )
        assertArrayEquals(expected, array)
    }

    @Test
    fun `encoding of recursive objects with collection works`() {
        val node = RecurseWithCollection(
            1,
            listOf(
                RecurseWithCollection(2, listOf()),
                RecurseWithCollection(3, listOf()),
            )
        )

        val encoder = composedEncoder<RecurseWithCollection> {
            encode(it.value)
            encode(it.collection)
        }

        val array = encoder.encode(node)

        val expected = byteArrayOf(
            1, 0, 0, 0, 2,
            2, 0, 0, 0, 0,
            3, 0, 0, 0, 0,
        )
        assertArrayEquals(expected, array)
    }

    @Test
    fun `encoding using encodeSelf works correctly`() {
        val encoder = composedEncoder<CustomNode> {
            if (it is CustomNode.Leaf) {
                encode(1.toByte())
                encode(it.value)
            } else if (it is CustomNode.Branch) {
                encode(0.toByte())
                encode(it.left)
                encode(it.right)
            }
        }

        val node = CustomNode.Branch(
            CustomNode.Leaf(1),
            CustomNode.Branch(
                CustomNode.Leaf(2),
                CustomNode.Leaf(3)
            )
        )

        val array = encoder.encode(node)

        val expected = byteArrayOf(
            0,
              1, 1,
              0,
                1, 2,
                1, 3,
        )

        assertArrayEquals(expected, array)
    }

    @Test
    fun `test impossible case for coverage`() {
        val impl = EncodingScopeImpl<Any>(
            EncoderOutput.Companion.from { },
            ArrayList(),
            ArrayDeque(),
            HashMap(),
            ByteOrder.BIG_ENDIAN,
            { _, _ -> }
        )

        assertThrows<AssertionError> {
            impl.encode("Hello World!")
        }
    }

}
