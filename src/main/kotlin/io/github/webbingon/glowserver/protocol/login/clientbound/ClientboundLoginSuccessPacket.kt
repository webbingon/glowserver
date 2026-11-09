package io.github.webbingon.glowserver.protocol.login.clientbound

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.model.GameProfile
import io.github.webbingon.glowserver.util.writeGameProfile
import io.github.webbingon.glowserver.util.writeUuid
import io.netty.buffer.ByteBuf
import kotlin.uuid.Uuid

data class ClientboundLoginSuccessPacket(
    val gameProfile: GameProfile,
    val sessionId: Uuid,
) : ClientboundPacket {
    override val type: PacketType = ClientboundLoginPacketType.LOGIN_SUCCESS

    override fun write(buf: ByteBuf) {
        buf.writeGameProfile(gameProfile)
        buf.writeUuid(sessionId)
    }
}
