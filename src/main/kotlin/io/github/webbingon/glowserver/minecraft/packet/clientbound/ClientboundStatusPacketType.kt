package io.github.webbingon.glowserver.minecraft.packet.clientbound

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketType

enum class ClientboundStatusPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.STATUS,
    override val direction: PacketDirection = PacketDirection.CLIENTBOUND
) : PacketType {
    STATUS_RESPONSE(0x0),
    PONG_RESPONSE(0x1)
}
