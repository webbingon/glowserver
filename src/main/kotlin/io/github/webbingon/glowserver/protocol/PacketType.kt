package io.github.webbingon.glowserver.protocol

interface PacketType {
    val id: Int
    val state: ConnectionState
    val direction: PacketDirection
}
