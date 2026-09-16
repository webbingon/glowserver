package io.github.webbingon.glowserver.protocol.common

interface PacketType {
    val id: Int
    val state: ConnectionState
    val direction: PacketDirection
}
