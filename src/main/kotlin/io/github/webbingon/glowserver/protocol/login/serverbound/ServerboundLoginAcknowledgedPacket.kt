package io.github.webbingon.glowserver.protocol.login.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket

data object ServerboundLoginAcknowledgedPacket : ServerboundPacket {
    override val type: PacketType = ServerboundLoginPacketType.LOGIN_ACKNOWLEDGED
}
