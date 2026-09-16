package io.github.webbingon.glowserver.protocol.serverbound

import io.github.webbingon.glowserver.protocol.ConnectionState
import io.github.webbingon.glowserver.protocol.PacketDirection
import io.github.webbingon.glowserver.protocol.PacketType

enum class ServerboundHandshakePacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.HANDSHAKE,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND,
) : PacketType {
    HANDSHAKE(0x0),
}
