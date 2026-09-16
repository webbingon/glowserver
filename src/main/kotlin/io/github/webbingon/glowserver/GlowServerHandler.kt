package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.minecraft.GameProfile
import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.KnownPack
import io.github.webbingon.glowserver.minecraft.STATE_KEY
import io.github.webbingon.glowserver.minecraft.packet.ClientboundFramedBufferFactory
import io.github.webbingon.glowserver.minecraft.packet.PacketDirection
import io.github.webbingon.glowserver.minecraft.packet.PacketTypeRegistry
import io.github.webbingon.glowserver.minecraft.ServerStatus
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundConfigurationPacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundHandshakePacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundLoginPacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundStatusPacketType
import io.github.webbingon.glowserver.util.readPrefixedArray
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readUuid
import io.github.webbingon.glowserver.util.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.io.FileNotFoundException
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
                        version = ServerStatus.Version(ServerConstants.MINECRAFT_VERSION, ServerConstants.PROTOCOL_VERSION),
                        players = ServerStatus.Players(ServerConstants.DEFAULT_MAX_PLAYERS, 0),
                        description = ServerStatus.Description(ServerConstants.DEFAULT_MOTD),
                        enforcesSecureChat = false
                    )

                    val framedBuf = ClientboundFramedBufferFactory.createStatusResponsePacket(ctx.alloc(), serverStatus)

                    ctx.writeAndFlush(framedBuf)
                }

                ServerboundStatusPacketType.PING_REQUEST -> {
                    val timestamp = packet.readLong()

                    println("[Status/Ping Request] Timestamp: $timestamp, Sending pong response...")

                    val framedBuf = ClientboundFramedBufferFactory.createPongResponsePacket(ctx.alloc(), timestamp)

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
                    val framedBuf = ClientboundFramedBufferFactory.createLoginSuccessPacket(ctx.alloc(), gameProfile)

                    ctx.writeAndFlush(framedBuf)
                }

                ServerboundLoginPacketType.LOGIN_ACKNOWLEDGED -> {
                    ctx.channel().attr(STATE_KEY).set(ConnectionState.CONFIGURATION)
                    println("[Login/Login Acknowledged] Switched the state to Configuration.")

                    val pluginMessageFramedBuf = ClientboundFramedBufferFactory.createPluginMessagePacketWithStringData(ctx.alloc(), "minecraft:brand", ServerConstants.SERVER_BRAND_NAME)

                    ctx.writeAndFlush(pluginMessageFramedBuf)

                    val knownPacksFramedBuf = ClientboundFramedBufferFactory.createKnownPacksPacket(ctx.alloc(), listOf(
                        KnownPack("minecraft", "core", ServerConstants.MINECRAFT_VERSION)
                    ))

                    ctx.writeAndFlush(knownPacksFramedBuf)
                }

                ServerboundConfigurationPacketType.PLUGIN_MESSAGE -> {
                    val identifier = packet.readString()

                    val data = if (identifier == "minecraft:brand") {
                        packet.readString()
                    } else {
                        null
                    }

                    println("[Configuration/Plugin Message] Identifier: $identifier, Data: $data, Received the packet.")
                }

                ServerboundConfigurationPacketType.CLIENT_INFORMATION -> {
                    val locale = packet.readString()
                    val viewDistance = packet.readByte().toInt()
                    val chatMode = packet.readVarInt()
                    val chatColors = packet.readBoolean()
                    val displayedSkinParts = packet.readUnsignedByte().toInt()
                    val mainHand = packet.readVarInt()
                    val doEnableTextFiltering = packet.readBoolean()
                    val allowServerListings = packet.readBoolean()
                    val particleStatus = packet.readVarInt()

                    println("[Configuration/Client Information] Locale: $locale, View distance: $viewDistance, Chat mode: $chatMode, Chat colors: $chatColors, Displayed Skin Parts: $displayedSkinParts, Main hand: $mainHand, Enable text filtering: $doEnableTextFiltering, Allow server listings: $allowServerListings, Particle status: $particleStatus, Received the packet.")
                }

                ServerboundConfigurationPacketType.KNOWN_PACKS -> {
                    val knownPacks = packet.readPrefixedArray {
                        val namespace = this.readString()
                        val id = this.readString()
                        val version = this.readString()

                        KnownPack(namespace, id, version)
                    }

                    println("[Configuration/Known Packs] Received the packet.")

                    for (pack in knownPacks) {
                        println("[Configuration/Known Pack] Namespace: ${pack.namespace}, ID: ${pack.id}, Version: ${pack.version}")
                    }

                    val resourceName = "registry_and_tags.bin"
                    val inputStream = object {}.javaClass.classLoader.getResourceAsStream(resourceName)
                        ?: throw FileNotFoundException("Resource $resourceName not found in resources folder.")

                    val bytes = inputStream.readBytes()
                    val buffer = ctx.alloc().buffer(bytes.size).writeBytes(bytes)

                    ctx.writeAndFlush(buffer).addListener { future ->
                        if(future.isSuccess) {
                            println("Sent registry and tags.")

                            val finishConfigurationFramedBuf = ClientboundFramedBufferFactory.createFinishConfigurationPacket(ctx.alloc())
                            ctx.writeAndFlush(finishConfigurationFramedBuf)
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
