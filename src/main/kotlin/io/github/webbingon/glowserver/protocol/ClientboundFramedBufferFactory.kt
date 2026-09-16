package io.github.webbingon.glowserver.protocol

import io.github.webbingon.glowserver.protocol.clientbound.ClientboundConfigurationPacketType
import io.github.webbingon.glowserver.protocol.clientbound.ClientboundLoginPacketType
import io.github.webbingon.glowserver.protocol.clientbound.ClientboundStatusPacketType
import io.github.webbingon.glowserver.protocol.model.GameProfile
import io.github.webbingon.glowserver.protocol.model.KnownPack
import io.github.webbingon.glowserver.protocol.model.ServerStatus
import io.github.webbingon.glowserver.util.toFramedBuffer
import io.github.webbingon.glowserver.util.writeGameProfile
import io.github.webbingon.glowserver.util.writePrefixedArray
import io.github.webbingon.glowserver.util.writeString
import io.github.webbingon.glowserver.util.writeUuid
import io.github.webbingon.glowserver.util.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

object ClientboundFramedBufferFactory {
    fun createStatusResponsePacket(
        allocator: ByteBufAllocator,
        serverStatus: ServerStatus,
    ): ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundStatusPacketType.STATUS_RESPONSE.id)
            payload.writeString(Json.encodeToString(serverStatus))

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }

    fun createPongResponsePacket(
        allocator: ByteBufAllocator,
        timestamp: Long,
    ): ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundStatusPacketType.PONG_RESPONSE.id)
            payload.writeLong(timestamp)

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }

    fun createLoginSuccessPacket(
        allocator: ByteBufAllocator,
        gameProfile: GameProfile,
    ): ByteBuf {
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

    fun createPluginMessagePacketWithStringData(
        allocator: ByteBufAllocator,
        identifier: String,
        data: String,
    ): ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundConfigurationPacketType.PLUGIN_MESSAGE.id)
            payload.writeString(identifier)
            payload.writeString(data)

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }

    fun createKnownPacksPacket(
        allocator: ByteBufAllocator,
        packs: Collection<KnownPack>,
    ): ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundConfigurationPacketType.KNOWN_PACKS.id)

            payload.writePrefixedArray(packs) {
                this.writeString(it.namespace)
                this.writeString(it.id)
                this.writeString(it.version)
            }

            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }

    fun createFinishConfigurationPacket(allocator: ByteBufAllocator): ByteBuf {
        val payload = allocator.buffer()

        try {
            payload.writeVarInt(ClientboundConfigurationPacketType.FINISH_CONFIGURATION.id)
            return payload.toFramedBuffer(allocator)
        } finally {
            payload.release()
        }
    }
}
