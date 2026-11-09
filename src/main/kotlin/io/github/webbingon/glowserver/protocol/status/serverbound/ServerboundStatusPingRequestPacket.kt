package io.github.webbingon.glowserver.protocol.status.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket
import io.netty.buffer.ByteBuf

data class ServerboundStatusPingRequestPacket(
    val timestamp: Long,
) : ServerboundPacket {
    override val type: PacketType = ServerboundStatusPacketType.PING_REQUEST

    companion object {
        fun read(buf: ByteBuf): ServerboundStatusPingRequestPacket {
            try {
                return ServerboundStatusPingRequestPacket(
                    buf.readLong(),
                )
            } finally {
                buf.release()
            }
        }
    }
}
