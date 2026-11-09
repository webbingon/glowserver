package io.github.webbingon.glowserver.protocol.codec

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.util.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class MinecraftPacketEncoder : MessageToByteEncoder<ClientboundPacket>() {
    override fun encode(
        ctx: ChannelHandlerContext,
        msg: ClientboundPacket,
        out: ByteBuf,
    ) {
        val body = ctx.alloc().buffer()

        try {
            body.writeVarInt(msg.type.id)
            msg.write(body)
            out.writeVarInt(body.readableBytes())
            out.writeBytes(body)
        } finally {
            body.release()
        }
    }

    override fun acceptOutboundMessage(msg: Any?): Boolean = msg !is ByteBuf
}
