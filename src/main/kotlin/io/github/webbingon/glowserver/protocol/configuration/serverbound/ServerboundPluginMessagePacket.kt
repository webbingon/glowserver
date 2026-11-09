package io.github.webbingon.glowserver.protocol.configuration.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket
import io.github.webbingon.glowserver.protocol.configuration.PluginMessagePacket
import io.github.webbingon.glowserver.util.readString
import io.netty.buffer.ByteBuf

data class ServerboundPluginMessagePacket(
    override val channel: String,
    override val data: ByteArray,
) : ServerboundPacket,
    PluginMessagePacket {
    override val type: PacketType = ServerboundConfigurationPacketType.PLUGIN_MESSAGE

    companion object {
        fun read(buf: ByteBuf): ServerboundPluginMessagePacket {
            try {
                val channel = buf.readString()
                val data = ByteArray(buf.readableBytes())

                buf.readBytes(data)

                return ServerboundPluginMessagePacket(
                    channel,
                    data,
                )
            } finally {
                buf.release()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerboundPluginMessagePacket

        if (channel != other.channel) return false
        if (!data.contentEquals(other.data)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channel.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
