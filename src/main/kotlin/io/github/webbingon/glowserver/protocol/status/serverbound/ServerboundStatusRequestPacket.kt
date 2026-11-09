package io.github.webbingon.glowserver.protocol.status.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket

data object ServerboundStatusRequestPacket : ServerboundPacket {
    override val type: PacketType = ServerboundStatusPacketType.STATUS_REQUEST
}
