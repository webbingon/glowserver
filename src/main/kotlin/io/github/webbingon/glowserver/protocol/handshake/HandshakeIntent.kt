package io.github.webbingon.glowserver.protocol.handshake

/**
 * State that the connection transition to.
 *
 * Both [HandshakeIntent.LOGIN] and [HandshakeIntent.TRANSFER] transition to "Login" state.
 * But the latter means the client connect because it received "Transfer" packet.
 *
 * @see io.github.webbingon.glowserver.protocol.handshake.serverbound.ServerboundHandshakePacket
 */
enum class HandshakeIntent(
    val id: Int,
) {
    STATUS(1),
    LOGIN(2),
    TRANSFER(3),
    ;

    companion object {
        private val map = entries.associateBy { it.id }

        fun fromId(id: Int): HandshakeIntent? = map[id]
    }
}
