package io.github.webbingon.glowserver

import io.github.webbingon.glowserver.protocol.MinecraftPacketDecoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class GlowServer(
    val port: Int,
) {
    private val bossGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
    private val workerGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

    fun run() {
        println("Starting server...")

        try {
            val bootstrap = ServerBootstrap()

            bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(
                    object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(
                                MinecraftPacketDecoder(),
                                LoggingHandler(LogLevel.INFO),
                                GlowServerHandler(),
                            )
                        }
                    },
                )

            val channelFuture =
                bootstrap
                    .bind(port)
                    .addListener {
                        println("Glowserver is listening on port $port")
                    }.sync()

            channelFuture.channel().closeFuture().sync()
        } finally {
            shutdown()
        }
    }

    fun shutdown() {
        if (!bossGroup.isShutdown) {
            println("Shutting down...")
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }
}
