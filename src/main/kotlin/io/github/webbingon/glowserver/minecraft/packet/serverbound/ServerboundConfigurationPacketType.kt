package io.github.webbingon.glowserver.minecraft.packet.serverbound

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketType

@Suppress("unused")
enum class ServerboundConfigurationPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.CONFIGURATION,
    override val direction: PacketDirection = PacketDirection.SERVERBOUND
) : PacketType {
    CLIENT_INFORMATION(0x0),
    COOKIE_RESPONSE(0x1),
    PLUGIN_MESSAGE(0x2),
    ACKNOWLEDGE_FINISH_CONFIGURATION(0x3),
    KEEP_ALIVE(0x4),
    PONG(0x5),
    RESOURCE_PACK_RESPONSE(0x6),
    KNOWN_PACKS(0x7),
    CUSTOM_CLICK_ACTION(0x8),
    ACCEPT_CODE_OF_CONDUCT(0x9)
}
