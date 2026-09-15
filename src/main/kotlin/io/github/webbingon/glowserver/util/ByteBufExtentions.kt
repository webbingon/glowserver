package io.github.webbingon.glowserver.util

import io.github.webbingon.glowserver.minecraft.GameProfile
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil
import kotlin.uuid.Uuid

fun ByteBuf.readVarInt(): Int {
    var value = 0

    for (position in (0..<32) step 7) {
        val currentByte = this.readByte().toInt()

        value = value or ((currentByte and 0x7F) shl position)

        if(currentByte and 0x80 == 0) {
            return value
        }
    }

    throw IllegalArgumentException("VarInt is too big. VarInt must be between 1 and 5 bytes.")
}

fun ByteBuf.readString() : String {
    val length = this.readVarInt()
    return this.readString(length, Charsets.UTF_8)
}

fun ByteBuf.readUuid() : Uuid {
    val msb = this.readLong()
    val lsb = this.readLong()
    return Uuid.fromLongs(msb, lsb)
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

inline fun <T> ByteBuf.writePrefixedArray(elements: Collection<T>, writeElement: ByteBuf.(T) -> Unit) {
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

        if(element.signature != null) {
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

fun ByteBuf.toFramedBuffer(allocator: ByteBufAllocator) : ByteBuf {
    val framed = allocator.buffer()
    framed.writeBytesWithVarInt(this)
    return framed
}
