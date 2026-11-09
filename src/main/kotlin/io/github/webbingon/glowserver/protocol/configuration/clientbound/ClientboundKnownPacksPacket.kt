package io.github.webbingon.glowserver.protocol.configuration.clientbound

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.configuration.KnownPacksPacket
import io.github.webbingon.glowserver.protocol.model.KnownPack
import io.github.webbingon.glowserver.util.writePrefixedArray
import io.github.webbingon.glowserver.util.writeString
import io.netty.buffer.ByteBuf

data class ClientboundKnownPacksPacket(
    override val knownPacks: List<KnownPack>,
) : ClientboundPacket,
    KnownPacksPacket {
    override val type: PacketType = ClientboundConfigurationPacketType.KNOWN_PACKS

    override fun write(buf: ByteBuf) {
        buf.writePrefixedArray(knownPacks) {
            this.writeString(it.namespace)
            this.writeString(it.id)
            this.writeString(it.version)
        }
    }
}
