package io.github.webbingon.glowserver.util

import io.netty.buffer.ByteBuf

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
