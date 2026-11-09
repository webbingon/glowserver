package io.github.webbingon.glowserver.protocol.configuration.serverbound

import io.github.webbingon.glowserver.protocol.common.PacketType
import io.github.webbingon.glowserver.protocol.common.ServerboundPacket
import io.github.webbingon.glowserver.protocol.configuration.ChatMode
import io.github.webbingon.glowserver.protocol.configuration.MainHand
import io.github.webbingon.glowserver.protocol.configuration.ParticleStatus
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readVarInt
import io.netty.buffer.ByteBuf

data class ServerboundClientInformationPacket(
    val locale: String,
    val viewDistance: Int,
    val chatMode: ChatMode,
    val chatColors: Boolean,
    val displayedSkinParts: Int,
    val mainHand: MainHand,
    val doEnableTextFiltering: Boolean,
    val doAllowServerListings: Boolean,
    val particleStatus: ParticleStatus,
) : ServerboundPacket {
    override val type: PacketType = ServerboundConfigurationPacketType.CLIENT_INFORMATION

    companion object {
        fun read(buf: ByteBuf): ServerboundClientInformationPacket {
            val locale = buf.readString()
            val viewDistance = buf.readByte().toInt()
            val chatModeId = buf.readVarInt()
            val chatMode =
                ChatMode.fromId(chatModeId) ?: throw IllegalArgumentException("Unknown chat mode ID $chatModeId")
            val chatColors = buf.readBoolean()
            val displayedSkinParts = buf.readUnsignedByte().toInt()
            val mainHandId = buf.readVarInt()
            val mainHand =
                MainHand.fromId(mainHandId) ?: throw IllegalArgumentException("Unknown main hand ID $mainHandId")
            val doEnableTextFiltering = buf.readBoolean()
            val doAllowServerListings = buf.readBoolean()
            val particleStatusId = buf.readVarInt()
            val particleStatus =
                ParticleStatus.fromId(particleStatusId)
                    ?: throw IllegalArgumentException("Unknown particle status ID $particleStatusId")

            return ServerboundClientInformationPacket(
                locale,
                viewDistance,
                chatMode,
                chatColors,
                displayedSkinParts,
                mainHand,
                doEnableTextFiltering,
                doAllowServerListings,
                particleStatus,
            )
        }
    }
}
