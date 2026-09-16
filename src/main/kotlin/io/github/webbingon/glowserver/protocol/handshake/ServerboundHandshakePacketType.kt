package io.github.webbingon.glowserver.protocol.handshake

import io.github.webbingon.glowserver.protocol.common.ConnectionState
import io.github.webbingon.glowserver.protocol.common.PacketDirection
import io.github.webbingon.glowserver.protocol.common.PacketType

enum class ServerboundHandshakePacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.HANDSHAKE,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND,
) : PacketType {
    HANDSHAKE(0x0),
}
