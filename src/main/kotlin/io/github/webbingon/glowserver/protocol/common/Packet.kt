package io.github.webbingon.glowserver.protocol.common

/**
 * Represents a packet which is included in the framed buffer.
 * @property type Type of the packet
 */
interface Packet {
    val type: PacketType
}
