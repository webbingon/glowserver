package io.github.webbingon.glowserver.protocol.serverbound

import io.github.webbingon.glowserver.protocol.ConnectionState
import io.github.webbingon.glowserver.protocol.PacketDirection
import io.github.webbingon.glowserver.protocol.PacketType

enum class ServerboundStatusPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.STATUS,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND,
) : PacketType {
    STATUS_REQUEST(0x0),
    PING_REQUEST(0x1),
}
