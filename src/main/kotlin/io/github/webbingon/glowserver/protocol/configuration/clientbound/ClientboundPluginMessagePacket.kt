package io.github.webbingon.glowserver.protocol.configuration.clientbound

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.configuration.PluginMessagePacket
import io.github.webbingon.glowserver.util.writeString
import io.netty.buffer.ByteBuf

data class ClientboundPluginMessagePacket(
    override val channel: String,
    override val data: ByteArray,
) : ClientboundPacket,
    PluginMessagePacket {
    override val type: PacketType = ClientboundConfigurationPacketType.PLUGIN_MESSAGE

    override fun write(buf: ByteBuf) {
        buf.writeString(channel)
        buf.writeBytes(data)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientboundPluginMessagePacket

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
