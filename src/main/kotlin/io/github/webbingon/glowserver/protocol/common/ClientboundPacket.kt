package io.github.webbingon.glowserver.protocol.common

import io.netty.buffer.ByteBuf

/**
 * Represents a packet sent to the client.
 */
interface ClientboundPacket : Packet {
    /**
     * Write the data of the instance to the provided [buf].
     *
     * NOTE: This method **MUST** write only packet data. It **MUST NOT** write the packet ID or packet length.
     *
     * @param buf The buffer to be written the data of the instance
     */
    fun write(buf: ByteBuf)
}
