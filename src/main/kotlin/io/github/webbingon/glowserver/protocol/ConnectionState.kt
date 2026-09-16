package io.github.webbingon.glowserver.protocol

import io.netty.util.AttributeKey

enum class ConnectionState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    CONFIGURATION,
    PLAY,
}

val STATE_KEY: AttributeKey<ConnectionState> = AttributeKey.valueOf("connection_state")
