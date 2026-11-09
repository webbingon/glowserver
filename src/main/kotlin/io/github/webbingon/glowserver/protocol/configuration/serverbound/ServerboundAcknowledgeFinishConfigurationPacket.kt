package io.github.webbingon.glowserver.protocol.configuration.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket

data object ServerboundAcknowledgeFinishConfigurationPacket : ServerboundPacket {
    override val type: PacketType = ServerboundConfigurationPacketType.ACKNOWLEDGE_FINISH_CONFIGURATION
}
