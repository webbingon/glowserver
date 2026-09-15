package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class GlowServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val packet = msg as ByteBuf

        try {
            val packetId = packet.readVarInt()

            if (packetId == 0x0) {
                val protocolVersion = packet.readVarInt()
                val serverAddress = packet.readString()
                val serverPort = packet.readUnsignedShort()
                val intent = packet.readVarInt()

                println("Protocol version: $protocolVersion, Server address: $serverAddress, Server port: $serverPort, Intent: $intent")
            }
        } finally {
            packet.release()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
