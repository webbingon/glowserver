package io.github.webbingon.glowserver.protocol.status

import io.github.webbingon.glowserver.protocol.common.ConnectionState
import io.github.webbingon.glowserver.protocol.common.PacketDirection
import io.github.webbingon.glowserver.protocol.common.PacketType

enum class ClientboundStatusPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.STATUS,
    override val direction: PacketDirection = PacketDirection.CLIENTBOUND,
) : PacketType {
    STATUS_RESPONSE(0x0),
    PONG_RESPONSE(0x1),
}
