package io.github.webbingon.glowserver.util

import io.github.webbingon.glowserver.protocol.model.GameProfile
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil
import kotlin.uuid.Uuid

fun ByteBuf.readVarInt(): Int {
    var value = 0

    for (position in (0..<32) step 7) {
        val currentByte = this.readByte().toInt()

        value = value or ((currentByte and 0x7F) shl position)

        if (currentByte and 0x80 == 0) {
            return value
        }
    }

    throw IllegalArgumentException("VarInt is too big. VarInt must be between 1 and 5 bytes.")
}

/**
 * Gets a [String] which is prefixed by the size in bytes as a VarInt.
 *
 * @param maxChars Maximum characters length
 * @return A decoded string
 * @throws IllegalArgumentException If the prefix size is wrong
 * @throws IllegalArgumentException If the string exceeds maximum byte length or character length
 */
fun ByteBuf.readString(maxChars: Int = 32767): String {
    val maxBytes = maxChars * 3
    val byteLength = this.readVarInt()

    require(byteLength >= 0) { "The byte length of string cannot be negative: $byteLength" }
    require(
        byteLength <= maxBytes,
    ) { "The byte length of string ($byteLength) exceeds maximum byte length ($maxBytes for max chars $maxChars)" }

    val readableBytes = this.readableBytes()

    require(
        byteLength <= readableBytes,
    ) { "String is expected $byteLength bytes, but only $readableBytes available" }

    val decodedString = this.readString(byteLength, Charsets.UTF_8)

    require(decodedString.length <= maxChars) {
        "String character length (${decodedString.length}) maximum length ($maxChars)"
    }

    return decodedString
}

fun ByteBuf.readUuid(): Uuid {
    val msb = this.readLong()
    val lsb = this.readLong()
    return Uuid.fromLongs(msb, lsb)
}

inline fun <T> ByteBuf.readPrefixedArray(readElement: ByteBuf.() -> T): List<T> {
    val count = this.readVarInt()
    val list = mutableListOf<T>()

    repeat(count) {
        list.add(readElement())
    }

    return list
}

fun ByteBuf.writeVarInt(value: Int) {
    var currentValue = value

    while (currentValue and 0x7F.inv() != 0) {
        this.writeByte((currentValue and 0x7F) or 0x80)

        currentValue = currentValue ushr 7
    }

    this.writeByte(currentValue)
}

fun ByteBuf.writeString(value: String) {
    this.writeVarInt(ByteBufUtil.utf8Bytes(value))
    ByteBufUtil.writeUtf8(this, value)
}

fun ByteBuf.writeUuid(uuid: Uuid) {
    val byteArray = uuid.toByteArray()
    this.writeBytes(byteArray)
}

inline fun <T> ByteBuf.writePrefixedArray(
    elements: Collection<T>,
    writeElement: ByteBuf.(T) -> Unit,
) {
    this.writeVarInt(elements.count())
    for (element in elements) {
        writeElement(element)
    }
}

fun ByteBuf.writeGameProfile(profile: GameProfile) {
    this.writeUuid(profile.uuid)
    this.writeString(profile.username)
    this.writePrefixedArray(profile.properties) { element ->
        this.writeString(element.name)
        this.writeString(element.value)

        if (element.signature != null) {
            this.writeBoolean(true)
            this.writeString(element.signature)
        } else {
            this.writeBoolean(false)
        }
    }
}

fun ByteBuf.writeBytesWithVarInt(src: ByteBuf) {
    this.writeVarInt(src.readableBytes())
    this.writeBytes(src)
}

fun ByteBuf.toFramedBuffer(allocator: ByteBufAllocator): ByteBuf {
    val framed = allocator.buffer()
    framed.writeBytesWithVarInt(this)
    return framed
}
