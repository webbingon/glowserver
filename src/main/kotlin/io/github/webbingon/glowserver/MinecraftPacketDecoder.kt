package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.util.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class MinecraftPacketDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext?, buf: ByteBuf, out: MutableList<Any>) {
        buf.markReaderIndex()

        val packetLength = try {
            buf.readVarInt()
        } catch (_: IndexOutOfBoundsException) {
            buf.resetReaderIndex()
            return
        }

        if (buf.readableBytes() < packetLength) {
            buf.resetReaderIndex()
            return
        }

        out.add(buf.readRetainedSlice(packetLength))
    }
}

