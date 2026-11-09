package io.github.webbingon.glowserver.protocol.login.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readUuid
import io.netty.buffer.ByteBuf
import kotlin.uuid.Uuid

data class ServerboundLoginStartPacket(
    val username: String,
    val uuid: Uuid,
) : ServerboundPacket {
    override val type: PacketType = ServerboundLoginPacketType.LOGIN_START

    companion object {
        fun read(buf: ByteBuf): ServerboundLoginStartPacket {
            try {
                val username = buf.readString()
                val uuid =
                    if (buf.readableBytes() >= 16) {
                        buf.readUuid()
                    } else {
                        Uuid.random()
                    }

                return ServerboundLoginStartPacket(
                    username,
                    uuid,
                )
            } finally {
                buf.release()
            }
        }
    }
}
