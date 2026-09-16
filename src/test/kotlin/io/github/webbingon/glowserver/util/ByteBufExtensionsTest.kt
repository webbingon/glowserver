package io.github.webbingon.glowserver.util

import io.github.webbingon.glowserver.minecraft.GameProfile
import io.github.webbingon.glowserver.minecraft.GameProfileProperty
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ByteBufExtensionsTest {
    @Test
    fun readVarInt() {
        val buffer = Unpooled.wrappedBuffer(byteArrayOf(0xAC.toByte(), 0x02))

        try {
            assertEquals(300, buffer.readVarInt())
            assertEquals(0, buffer.readableBytes())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readVarInt_whenVarIntIsTooBig() {
        val buffer =
            Unpooled.wrappedBuffer(
                byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte()),
            )

        try {
            assertFailsWith<IllegalArgumentException> { buffer.readVarInt() }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readVarInt_whenBufferIsTooShort() {
        val buffer = Unpooled.wrappedBuffer(byteArrayOf(0x80.toByte()))

        try {
            assertFailsWith<IndexOutOfBoundsException> { buffer.readVarInt() }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readString() {
        val buffer = Unpooled.buffer()
        buffer.writeString("こんにちは")

        try {
            assertEquals("こんにちは", buffer.readString())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readString_whenStringLengthIsNegative() {
        val buffer = Unpooled.buffer()
        buffer.writeVarInt(-1)

        try {
            assertFailsWith<StringIndexOutOfBoundsException> { buffer.readString() }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readUuid() {
        val uuid = Uuid.fromLongs(0x0123456789ABCDEFL, -0x0123456789ABCDEFL)
        val buffer = Unpooled.buffer()
        buffer.writeUuid(uuid)

        try {
            assertEquals(uuid, buffer.readUuid())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readUuid_whenBufferIsTooShort() {
        val buffer = Unpooled.buffer()
        buffer.writeLong(1L)

        try {
            assertFailsWith<IndexOutOfBoundsException> { buffer.readUuid() }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readPrefixedArray() {
        val buffer = Unpooled.buffer()
        buffer.writeVarInt(3)
        buffer.writeByte(10)
        buffer.writeByte(20)
        buffer.writeByte(30)

        try {
            assertEquals(listOf<Short>(10, 20, 30), buffer.readPrefixedArray<Short> { readUnsignedByte() })
        } finally {
            buffer.release()
        }
    }

    @Test
    fun readPrefixedArray_whenElementBytesAreMissing() {
        val buffer = Unpooled.buffer()
        buffer.writeVarInt(2)
        buffer.writeByte(10)

        try {
            assertFailsWith<IndexOutOfBoundsException> {
                buffer.readPrefixedArray<Short> { readUnsignedByte() }
            }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writeVarInt() {
        val buffer = Unpooled.buffer()

        try {
            buffer.writeVarInt(300)
            assertContentEquals(byteArrayOf(0xAC.toByte(), 0x02), readAllBytes(buffer))
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writeVarInt_whenValueIsNegative() {
        val buffer = Unpooled.buffer()

        try {
            buffer.writeVarInt(-1)
            assertContentEquals(
                byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F),
                readAllBytes(buffer),
            )
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writeString() {
        val buffer = Unpooled.buffer()

        try {
            buffer.writeString("こんにちは")
            assertEquals("こんにちは", buffer.readString())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writeUuid() {
        val uuid = Uuid.fromLongs(1L, 2L)
        val buffer = Unpooled.buffer()

        try {
            buffer.writeUuid(uuid)
            assertEquals(uuid, buffer.readUuid())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writePrefixedArray() {
        val buffer = Unpooled.buffer()

        try {
            buffer.writePrefixedArray(listOf("one", "two")) { writeString(it) }
            assertEquals(2, buffer.readVarInt())
            assertEquals("one", buffer.readString())
            assertEquals("two", buffer.readString())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writeGameProfile() {
        val profile =
            GameProfile(
                Uuid.fromLongs(1L, 2L),
                "player",
                listOf(
                    GameProfileProperty("textures", "value", "signature"),
                    GameProfileProperty("empty", "value", null),
                ),
            )
        val buffer = Unpooled.buffer()

        try {
            buffer.writeGameProfile(profile)
            assertEquals(profile.uuid, buffer.readUuid())
            assertEquals(profile.username, buffer.readString())
            assertEquals(2, buffer.readVarInt())
            assertEquals("textures", buffer.readString())
            assertEquals("value", buffer.readString())
            assertTrue(buffer.readBoolean())
            assertEquals("signature", buffer.readString())
            assertEquals("empty", buffer.readString())
            assertEquals("value", buffer.readString())
            assertTrue(!buffer.readBoolean())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun writeBytesWithVarInt() {
        val source = Unpooled.wrappedBuffer(byteArrayOf(1, 2, 3))
        val destination = Unpooled.buffer()

        try {
            destination.writeBytesWithVarInt(source)
            assertEquals(0, source.readableBytes())
            assertEquals(3, destination.readVarInt())
            assertContentEquals(byteArrayOf(1, 2, 3), readAllBytes(destination))
        } finally {
            source.release()
            destination.release()
        }
    }

    @Test
    fun writeBytesWithVarInt_whenSourceIsEmpty() {
        val source = Unpooled.buffer()
        val destination = Unpooled.buffer()

        try {
            destination.writeBytesWithVarInt(source)
            assertEquals(0, destination.readVarInt())
            assertEquals(0, destination.readableBytes())
        } finally {
            source.release()
            destination.release()
        }
    }

    @Test
    fun toFramedBuffer() {
        val source = Unpooled.wrappedBuffer(byteArrayOf(4, 5, 6))

        try {
            val framed = source.toFramedBuffer(ByteBufAllocator.DEFAULT)

            try {
                assertEquals(0, source.readableBytes())
                assertEquals(3, framed.readVarInt())
                assertContentEquals(byteArrayOf(4, 5, 6), readAllBytes(framed))
            } finally {
                framed.release()
            }
        } finally {
            source.release()
        }
    }

    @Test
    fun toFramedBuffer_whenSourceIsEmpty() {
        val source = Unpooled.buffer()

        try {
            val framed = source.toFramedBuffer(ByteBufAllocator.DEFAULT)

            try {
                assertEquals(0, framed.readVarInt())
                assertEquals(0, framed.readableBytes())
            } finally {
                framed.release()
            }
        } finally {
            source.release()
        }
    }

    private fun readAllBytes(buffer: ByteBuf): ByteArray {
        val bytes = ByteArray(buffer.readableBytes())
        buffer.readBytes(bytes)
        return bytes
    }
}
