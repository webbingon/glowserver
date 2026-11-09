package io.github.webbingon.glowserver.protocol.configuration.clientbound

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.protocol.common.PacketType
import io.netty.buffer.ByteBuf

data object ClientboundFinishConfigurationPacket : ClientboundPacket {
    override val type: PacketType = ClientboundConfigurationPacketType.FINISH_CONFIGURATION

    override fun write(buf: ByteBuf) {
    }
}
