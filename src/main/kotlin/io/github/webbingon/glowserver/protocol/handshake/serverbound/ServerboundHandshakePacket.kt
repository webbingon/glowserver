package io.github.webbingon.glowserver.protocol.handshake.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket
import io.github.webbingon.glowserver.protocol.handshake.HandshakeIntent
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readVarInt
import io.netty.buffer.ByteBuf

/**
 * A packet to change the connection to the specific state.
 *
 * @property protocolVersion The protocol version of the client trying to connect
 * @property serverAddress The address of the server
 * @property serverPort The port of the server
 * @property intent Which state transition to (1: [HandshakeIntent.STATUS], 2: [HandshakeIntent.LOGIN], 3: [HandshakeIntent.TRANSFER]) Both [HandshakeIntent.LOGIN] and [HandshakeIntent.TRANSFER] intent transition to "login" state.
 *
 * @see HandshakeIntent
 */
data class ServerboundHandshakePacket(
    val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: Int,
    val intent: HandshakeIntent,
) : ServerboundPacket {
    override val type: PacketType = ServerboundHandshakePacketType.HANDSHAKE

    companion object {
        /**
         * Create the instance from provided [buf].
         *
         * @param buf The buffer contains the raw packet data without the packet ID and the packet length.
         * @return The decoded [ServerboundHandshakePacket]
         * @throws IllegalArgumentException If the length of the server address is greater than 255
         * @throws IllegalArgumentException If an unknown intent ID is read from the buffer
         */
        fun read(buf: ByteBuf): ServerboundHandshakePacket {
            val protocolVersion = buf.readVarInt()
            val serverAddress = buf.readString(255)
            val serverPort = buf.readUnsignedShort()
            val intentId = buf.readVarInt()
            val intent =
                HandshakeIntent.fromId(intentId) ?: throw IllegalArgumentException("Unknown Intent ID $intentId")

            return ServerboundHandshakePacket(
                protocolVersion,
                serverAddress,
                serverPort,
                intent,
            )
        }
    }
}
