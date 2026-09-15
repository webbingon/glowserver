package io.github.webbingon.glowserver.minecraft.packet.serverbound

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketType

enum class ServerboundHandshakePacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.HANDSHAKE,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND
) : PacketType {
    HANDSHAKE(0x0);
}
