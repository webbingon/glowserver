package io.github.webbingon.glowserver.minecraft.packet

import io.github.webbingon.glowserver.minecraft.ConnectionState

interface PacketType {
    val id: Int
    val state: ConnectionState
    val direction: PacketDirection
}
