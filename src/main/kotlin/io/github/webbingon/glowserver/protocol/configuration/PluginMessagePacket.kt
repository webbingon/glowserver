package io.github.webbingon.glowserver.protocol.configuration

import io.github.webbingon.glowserver.protocol.common.Packet

interface PluginMessagePacket : Packet {
    val channel: String
    val data: ByteArray
}
