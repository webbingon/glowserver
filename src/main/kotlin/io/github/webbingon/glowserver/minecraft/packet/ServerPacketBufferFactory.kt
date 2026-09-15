package io.github.webbingon.glowserver.minecraft.packet

import io.github.webbingon.glowserver.minecraft.GameProfile
import io.github.webbingon.glowserver.minecraft.ServerStatus
import io.github.webbingon.glowserver.minecraft.packet.clientbound.ClientboundLoginPacketType
import io.github.webbingon.glowserver.minecraft.packet.clientbound.ClientboundStatusPacketType
import io.github.webbingon.glowserver.util.toFramedBuffer
import io.github.webbingon.glowserver.util.writeGameProfile
import io.github.webbingon.glowserver.util.writeString
import io.github.webbingon.glowserver.util.writeUuid
import io.github.webbingon.glowserver.util.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

object ServerPacketBufferFactory {
    fun createStatusResponsePacket (allocator: ByteBufAllocator, serverStatus: ServerStatus) : ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundStatusPacketType.STATUS_RESPONSE.id)
            payload.writeString(Json.encodeToString(serverStatus))

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }

    fun createPongResponsePacket (allocator: ByteBufAllocator, timestamp: Long) : ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundStatusPacketType.PONG_RESPONSE.id)
            payload.writeLong(timestamp)

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }

    fun createLoginSuccessPacket (allocator: ByteBufAllocator, gameProfile: GameProfile) : ByteBuf {
        val payload = allocator.buffer()

        try {
            val sessionId = Uuid.random()

            payload.writeVarInt(ClientboundLoginPacketType.LOGIN_SUCCESS.id)
            payload.writeGameProfile(gameProfile)
            payload.writeUuid(sessionId)

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }
}
