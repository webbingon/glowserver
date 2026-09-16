package io.github.webbingon.glowserver.protocol.status

import io.github.webbingon.glowserver.protocol.common.ConnectionState
import io.github.webbingon.glowserver.protocol.common.PacketDirection
import io.github.webbingon.glowserver.protocol.common.PacketType

enum class ServerboundStatusPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.STATUS,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND,
) : PacketType {
    STATUS_REQUEST(0x0),
    PING_REQUEST(0x1),
}
