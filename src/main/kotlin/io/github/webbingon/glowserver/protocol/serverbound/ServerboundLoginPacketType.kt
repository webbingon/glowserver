package io.github.webbingon.glowserver.protocol.serverbound

import io.github.webbingon.glowserver.protocol.ConnectionState
import io.github.webbingon.glowserver.protocol.PacketDirection
import io.github.webbingon.glowserver.protocol.PacketType

@Suppress("unused")
enum class ServerboundLoginPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.LOGIN,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND,
) : PacketType {
    LOGIN_START(0x0),
    ENCRYPTION_RESPONSE(0x1),
    LOGIN_PLUGIN_RESPONSE(0x2),
    LOGIN_ACKNOWLEDGED(0x3),
    COOKIE_RESPONSE(0x4),
}
