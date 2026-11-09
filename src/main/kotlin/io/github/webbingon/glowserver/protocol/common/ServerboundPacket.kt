package io.github.webbingon.glowserver.protocol.common

/**
 * Represents a packet sent to the server.
 *
 * Concrete classes should implement `read` method in their companion object to create the instance from the framed buffer ([io.netty.buffer.ByteBuf]).
 *
 * NOTE: Provided buffer to `read` method **MUST NOT** include the packet ID or the packet length.
 */
interface ServerboundPacket : Packet
