package io.github.webbingon.glowserver.protocol.status.clientbound

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.protocol.common.PacketType
import io.netty.buffer.ByteBuf

data class ClientboundStatusPongResponsePacket(
    val timestamp: Long,
) : ClientboundPacket {
    override val type: PacketType = ClientboundStatusPacketType.PONG_RESPONSE

    override fun write(buf: ByteBuf) {
        buf.writeLong(timestamp)
    }
}
