package io.github.webbingon.glowserver.minecraft.packet.clientbound

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketType

enum class ClientboundConfigurationPacketType(
    override val id: Int,
    override val state: ConnectionState = ConnectionState.CONFIGURATION,
    override val direction: PacketDirection = PacketDirection.CLIENTBOUND
) : PacketType{
    COOKIE_REQUEST(0x0),
    PLUGIN_MESSAGE(0x1),
    DISCONNECT(0x2),
    FINISH_CONFIGURATION(0x3),
    KEEP_ALIVE(0x4),
    PING(0x5),
    RESET_CHAT(0x6),
    REGISTRY_DATA(0x7),
    REMOVE_RESOURCE_PACK(0x8),
    ADD_RESOURCE_PACK(0x9),
    STORE_COOKIE(0xA),
    TRANSFER(0xB),
    FEATURE_FLAG(0xC),
    UPDATE_TAGS(0xD),
    KNOWN_PACKS(0xE),
    CUSTOM_REPORT_DETAILS(0xF),
    SERVER_LINKS(0x10),
    CLEAR_DIALOG(0x11),
    SHOW_DIALOG(0x12),
    CODE_OF_CONDUCT(0x13)
}
