package io.github.webbingon.glowserver.minecraft.packet.clientbound

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketType

@Suppress("unused")
enum class ClientboundLoginPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.LOGIN,
    override val direction: PacketDirection = PacketDirection.CLIENTBOUND
) : PacketType {
    LOGIN_DISCONNECT(0x0),
    ENCRYPTION_REQUEST(0x1),
    LOGIN_SUCCESS(0x2),
    SET_COMPRESSION(0x3),
    LOGIN_PLUGIN_REQUEST(0x4),
    COOKIE_REQUEST(0x5);
}
