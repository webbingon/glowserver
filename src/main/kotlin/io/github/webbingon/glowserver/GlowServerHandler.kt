package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.minecraft.GameProfile
import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.STATE_KEY
import io.github.webbingon.glowserver.minecraft.packet.ServerPacketBufferFactory
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketTypeRegistry
import io.github.webbingon.glowserver.minecraft.packet.ServerStatus
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundHandshakePacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundLoginPacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundStatusPacketType
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readUuid
import io.github.webbingon.glowserver.util.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import kotlin.collections.emptyList
import kotlin.uuid.Uuid

class GlowServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.channel().attr(STATE_KEY).set(ConnectionState.HANDSHAKE)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val packet = msg as ByteBuf

        try {
            val currentState = ctx.channel().attr(STATE_KEY).get()
            val packetId = packet.readVarInt()
            val packetType = PacketTypeRegistry.get(packetId, currentState, PacketDirection.SERVERBOUND)

            when (packetType) {
                ServerboundHandshakePacketType.HANDSHAKE -> {
                    val protocolVersion = packet.readVarInt()
                    val serverAddress = packet.readString()
                    val serverPort = packet.readUnsignedShort()
                    val intent = packet.readVarInt()

                    val state = ctx.channel().attr(STATE_KEY)

                    if (intent == 1) {
                        state.set(ConnectionState.STATUS)
                    } else if (intent == 2 || intent == 3) {
                        state.set(ConnectionState.LOGIN)
                    }

                    println("[Handshake/Handshake] Protocol version: $protocolVersion, Server address: $serverAddress, Server port: $serverPort, Intent: $intent")
                }

                ServerboundStatusPacketType.STATUS_REQUEST -> {
                    println("[Status/Status Request] Sending response...")

                    val serverStatus = ServerStatus(
                        version = ServerStatus.Version(ServerConstants.SERVER_VERSION, ServerConstants.PROTOCOL_VERSION),
                        players = ServerStatus.Players(ServerConstants.DEFAULT_MAX_PLAYERS, 0),
                        description = ServerStatus.Description(ServerConstants.DEFAULT_MOTD),
                        enforcesSecureChat = false
                    )

                    val framedBuf = ServerPacketBufferFactory.createStatusResponsePacket(ctx.alloc(), serverStatus)

                    ctx.writeAndFlush(framedBuf)
                }

                ServerboundStatusPacketType.PING_REQUEST -> {
                    val timestamp = packet.readLong()

                    println("[Status/Ping Request] Timestamp: $timestamp, Sending pong response...")

                    val framedBuf = ServerPacketBufferFactory.createPongResponsePacket(ctx.alloc(), timestamp)

                    ctx.writeAndFlush(framedBuf)
                }

                ServerboundLoginPacketType.LOGIN_START -> {
                    val username = packet.readString()

                    val uuid = if (packet.readableBytes() >= 16) {
                        packet.readUuid()
                    } else {
                        Uuid.random()
                    }

                    println("[Login/Login Start] Player name: $username, Player UUID: $uuid")

                    val gameProfile = GameProfile(uuid, username, emptyList())
                    val framedBuf = ServerPacketBufferFactory.createLoginSuccessPacket(ctx.alloc(), gameProfile)

                    ctx.writeAndFlush(framedBuf)
                }

                ServerboundLoginPacketType.LOGIN_ACKNOWLEDGED -> {
                    println("[Login/Login Acknowledged] Switched the state to Configuration.")
                    ctx.channel().attr(STATE_KEY).set(ConnectionState.CONFIGURATION)
                }

                null -> {
                    println("[Unknown Packet] State: $currentState, Packet ID: 0x${packetId.toString(16)}")
                }

                else -> {
                    println("[Unhandled Packet] State: $currentState, Packet Type: $packetType, ID: 0x${packetId.toString(16)}")
                }
            }
        } finally {
            packet.release()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
