package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.minecraft.GameProfile
import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.STATE_KEY
import io.github.webbingon.glowserver.util.readString
import io.github.webbingon.glowserver.util.readUuid
import io.github.webbingon.glowserver.util.readVarInt
import io.github.webbingon.glowserver.util.writeBytesWithVarInt
import io.github.webbingon.glowserver.util.writeGameProfile
import io.github.webbingon.glowserver.util.writeUuid
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

            when (currentState) {
                ConnectionState.HANDSHAKE -> {
                    if (packetId == 0x0) {
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

                        println("Protocol version: $protocolVersion, Server address: $serverAddress, Server port: $serverPort, Intent: $intent")
                    }
                }

                ConnectionState.STATUS -> TODO()

                ConnectionState.LOGIN -> {
                    when (packetId) {
                        0x0 -> {
                            val username = packet.readString()

                            val uuid = if (packet.readableBytes() >= 16) {
                                packet.readUuid()
                            } else {
                                Uuid.random()
                            }

                            println("Player name: $username, Player UUID: $uuid")

                            val payload = ctx.alloc().buffer()

                            val sessionId = Uuid.random()

                            payload.writeByte(0x2)
                            payload.writeGameProfile(GameProfile(uuid, username, emptyList()))
                            payload.writeUuid(sessionId)

                            val sendPacket = ctx.alloc().buffer()

                            sendPacket.writeBytesWithVarInt(payload)

                            ctx.writeAndFlush(sendPacket)
                        }

                        0x3 -> {
                            println("Login acknowledged.")
                            ctx.channel().attr(STATE_KEY).set(ConnectionState.CONFIGURATION)
                        }
                    }
                }

                ConnectionState.CONFIGURATION -> TODO()
                ConnectionState.PLAY -> TODO()
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
