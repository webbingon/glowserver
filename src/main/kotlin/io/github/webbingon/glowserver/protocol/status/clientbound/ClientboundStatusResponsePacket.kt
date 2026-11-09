package io.github.webbingon.glowserver.protocol.status.clientbound

import io.github.webbingon.glowserver.protocol.common.ClientboundPacket
import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.model.ServerStatus
import io.github.webbingon.glowserver.util.writeString
import io.netty.buffer.ByteBuf
import kotlinx.serialization.json.Json

data class ClientboundStatusResponsePacket(
    val serverStatus: ServerStatus,
) : ClientboundPacket {
    override val type: PacketType = ClientboundStatusPacketType.STATUS_RESPONSE

    override fun write(buf: ByteBuf) {
        buf.writeString(Json.encodeToString(serverStatus))
    }
}
