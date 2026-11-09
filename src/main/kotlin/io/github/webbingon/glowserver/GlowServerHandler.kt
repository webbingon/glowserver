package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.protocol.common.ConnectionState
import io.github.webbingon.glowserver.protocol.common.PacketDirection
import io.github.webbingon.glowserver.protocol.common.PacketTypeRegistry
import io.github.webbingon.glowserver.protocol.common.STATE_KEY
import io.github.webbingon.glowserver.protocol.configuration.clientbound.ClientboundFinishConfigurationPacket
import io.github.webbingon.glowserver.protocol.configuration.clientbound.ClientboundKnownPacksPacket
import io.github.webbingon.glowserver.protocol.configuration.clientbound.ClientboundPluginMessagePacket
import io.github.webbingon.glowserver.protocol.configuration.serverbound.ServerboundClientInformationPacket
import io.github.webbingon.glowserver.protocol.configuration.serverbound.ServerboundConfigurationPacketType
import io.github.webbingon.glowserver.protocol.handshake.HandshakeIntent
import io.github.webbingon.glowserver.protocol.handshake.serverbound.ServerboundHandshakePacket
import io.github.webbingon.glowserver.protocol.handshake.serverbound.ServerboundHandshakePacketType
import io.github.webbingon.glowserver.protocol.login.clientbound.ClientboundLoginSuccessPacket
import io.github.webbingon.glowserver.protocol.login.serverbound.ServerboundLoginPacketType
import io.github.webbingon.glowserver.protocol.model.GameProfile
import io.github.webbingon.glowserver.protocol.model.KnownPack
import io.github.webbingon.glowserver.protocol.model.ServerStatus
import io.github.webbingon.glowserver.protocol.status.clientbound.ClientboundStatusPongResponsePacket
import io.github.webbingon.glowserver.protocol.status.clientbound.ClientboundStatusResponsePacket
import io.github.webbingon.glowserver.protocol.status.serverbound.ServerboundStatusPacketType
import io.github.webbingon.glowserver.util.readPrefixedArray
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readUuid
import io.github.webbingon.glowserver.util.readVarInt
import io.github.webbingon.glowserver.util.writeString
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.io.FileNotFoundException
import kotlin.collections.emptyList
import kotlin.uuid.Uuid

class GlowServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.channel().attr(STATE_KEY).set(ConnectionState.HANDSHAKE)
    }

    override fun channelRead(
        ctx: ChannelHandlerContext,
        msg: Any,
    ) {
        val buf = msg as ByteBuf

        try {
            val currentState = ctx.channel().attr(STATE_KEY).get()
            val packetId = buf.readVarInt()
            when (val packetType = PacketTypeRegistry.get(packetId, currentState, PacketDirection.SERVERBOUND)) {
                ServerboundHandshakePacketType.HANDSHAKE -> {
                    val packet = ServerboundHandshakePacket.read(buf)

                    val state = ctx.channel().attr(STATE_KEY)

                    if (packet.intent == HandshakeIntent.STATUS) {
                        state.set(ConnectionState.STATUS)
                    } else if (packet.intent == HandshakeIntent.TRANSFER || packet.intent == HandshakeIntent.LOGIN) {
                        state.set(ConnectionState.LOGIN)
                    }

                    println(
                        "[Handshake/Handshake] Protocol version: ${packet.protocolVersion}, Server address: ${packet.serverAddress}, Server port: ${packet.serverPort}, Intent: ${packet.intent}",
                    )
                }

                ServerboundStatusPacketType.STATUS_REQUEST -> {
                    println("[Status/Status Request] Sending response...")

                    ctx.writeAndFlush(
                        ClientboundStatusResponsePacket(
                            ServerStatus(
                                version =
                                    ServerStatus.Version(
                                        ServerConstants.MINECRAFT_VERSION,
                                        ServerConstants.PROTOCOL_VERSION,
                                    ),
                                players = ServerStatus.Players(ServerConstants.DEFAULT_MAX_PLAYERS, 0),
                                description = ServerStatus.Description(ServerConstants.DEFAULT_MOTD),
                                enforcesSecureChat = false,
                            ),
                        ),
                    )
                }

                ServerboundStatusPacketType.PING_REQUEST -> {
                    val timestamp = buf.readLong()

                    println("[Status/Ping Request] Timestamp: $timestamp, Sending pong response...")

                    ctx.writeAndFlush(ClientboundStatusPongResponsePacket(timestamp))
                }

                ServerboundLoginPacketType.LOGIN_START -> {
                    val username = buf.readString()

                    val uuid =
                        if (buf.readableBytes() >= 16) {
                            buf.readUuid()
                        } else {
                            Uuid.random()
                        }

                    println("[Login/Login Start] Player name: $username, Player UUID: $uuid")

                    ctx.writeAndFlush(
                        ClientboundLoginSuccessPacket(
                            GameProfile(uuid, username, emptyList()),
                            uuid,
                        ),
                    )
                }

                ServerboundLoginPacketType.LOGIN_ACKNOWLEDGED -> {
                    ctx.channel().attr(STATE_KEY).set(ConnectionState.CONFIGURATION)
                    println("[Login/Login Acknowledged] Switched the state to Configuration.")

                    val pluginMessageData = ctx.alloc().buffer()
                    pluginMessageData.writeString(ServerConstants.SERVER_BRAND_NAME)

                    ctx.writeAndFlush(
                        ClientboundPluginMessagePacket(
                            "minecraft:brand",
                            ByteBufUtil.getBytes(pluginMessageData),
                        ),
                    )

                    pluginMessageData.release()

                    ctx.writeAndFlush(
                        ClientboundKnownPacksPacket(
                            listOf(
                                KnownPack("minecraft", "core", ServerConstants.MINECRAFT_VERSION),
                            ),
                        ),
                    )
                }

                ServerboundConfigurationPacketType.PLUGIN_MESSAGE -> {
                    val identifier = buf.readString()

                    val data =
                        if (identifier == "minecraft:brand") {
                            buf.readString()
                        } else {
                            null
                        }

                    println("[Configuration/Plugin Message] Identifier: $identifier, Data: $data, Received the packet.")
                }

                ServerboundConfigurationPacketType.CLIENT_INFORMATION -> {
                    val packet = ServerboundClientInformationPacket.read(buf)

                    println(
                        "[Configuration/Client Information] Locale: ${packet.locale}, View distance: ${packet.viewDistance}, Chat mode: ${packet.chatMode}, Chat colors: ${packet.chatColors}, Displayed Skin Parts: ${packet.displayedSkinParts}, Main hand: ${packet.mainHand}, Enable text filtering: ${packet.doEnableTextFiltering}, Allow server listings: ${packet.doAllowServerListings}, Particle status: ${packet.particleStatus}, Received the packet.",
                    )
                }

                ServerboundConfigurationPacketType.KNOWN_PACKS -> {
                    val knownPacks =
                        buf.readPrefixedArray {
                            val namespace = this.readString()
                            val id = this.readString()
                            val version = this.readString()

                            KnownPack(namespace, id, version)
                        }

                    println("[Configuration/Known Packs] Received the packet.")

                    for ((namespace, id, version) in knownPacks) {
                        println(
                            "[Configuration/Known Pack] Namespace: $namespace, ID: $id, Version: $version",
                        )
                    }

                    val resourceName = "registry_and_tags.bin"
                    val inputStream =
                        object {}.javaClass.classLoader.getResourceAsStream(resourceName)
                            ?: throw FileNotFoundException("Resource $resourceName not found in resources folder.")

                    val bytes = inputStream.readBytes()
                    val buffer = ctx.alloc().buffer(bytes.size).writeBytes(bytes)

                    ctx.writeAndFlush(buffer).addListener { future ->
                        if (future.isSuccess) {
                            println("Sent registry and tags.")
                            ctx.writeAndFlush(ClientboundFinishConfigurationPacket)
                        } else {
                            println("Failed to send registry and tags.")
                            ctx.close()
                        }
                    }
                }

                ServerboundConfigurationPacketType.ACKNOWLEDGE_FINISH_CONFIGURATION -> {
                    ctx.channel().attr(STATE_KEY).set(ConnectionState.PLAY)
                    println("[Configuration/Acknowledge Finish Configuration] Switched to the state to Play.")
                }

                null -> {
                    println("[Unknown Packet] State: $currentState, Packet ID: 0x${packetId.toString(16)}")
                }

                else -> {
                    println(
                        "[Unhandled Packet] State: $currentState, Packet Type: $packetType, ID: 0x${packetId.toString(
                            16,
                        )}",
                    )
                }
            }
        } finally {
            buf.release()
        }
    }

    override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        cause.printStackTrace()
        ctx.close()
    }
}
