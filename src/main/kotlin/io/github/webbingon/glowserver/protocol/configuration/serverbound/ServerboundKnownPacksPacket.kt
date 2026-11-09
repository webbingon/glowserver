package io.github.webbingon.glowserver.protocol.configuration.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket
import io.github.webbingon.glowserver.protocol.configuration.KnownPacksPacket
import io.github.webbingon.glowserver.protocol.model.KnownPack
import io.github.webbingon.glowserver.util.readPrefixedArray
import io.github.webbingon.glowserver.util.readString
import io.netty.buffer.ByteBuf

data class ServerboundKnownPacksPacket(
    override val knownPacks: List<KnownPack>,
) : ServerboundPacket,
    KnownPacksPacket {
    override val type: PacketType = ServerboundConfigurationPacketType.KNOWN_PACKS

    companion object {
        fun read(buf: ByteBuf): ServerboundKnownPacksPacket {
            try {
                val knownPacks =
                    buf.readPrefixedArray {
                        val namespace = this.readString()
                        val id = this.readString()
                        val version = this.readString()

                        KnownPack(namespace, id, version)
                    }

                return ServerboundKnownPacksPacket(knownPacks)
            } finally {
                buf.release()
            }
        }
    }
}
