package io.github.webbingon.glowserver.minecraft.packet.serverbound

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketType

enum class ServerboundStatusPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.STATUS,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND,
) : PacketType {
    STATUS_REQUEST(0x0),
    PING_REQUEST(0x1)
}
