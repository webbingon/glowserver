package io.github.webbingon.glowserver.protocol.clientbound

import io.github.webbingon.glowserver.protocol.ConnectionState
import io.github.webbingon.glowserver.protocol.PacketDirection
import io.github.webbingon.glowserver.protocol.PacketType

enum class ClientboundStatusPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.STATUS,
    override val direction: PacketDirection = PacketDirection.CLIENTBOUND,
) : PacketType {
    STATUS_RESPONSE(0x0),
    PONG_RESPONSE(0x1),
}
