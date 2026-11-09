package io.github.webbingon.glowserver.protocol.common

import io.github.webbingon.glowserver.protocol.configuration.clientbound.ClientboundConfigurationPacketType
import io.github.webbingon.glowserver.protocol.configuration.serverbound.ServerboundConfigurationPacketType
import io.github.webbingon.glowserver.protocol.handshake.serverbound.ServerboundHandshakePacketType
import io.github.webbingon.glowserver.protocol.login.clientbound.ClientboundLoginPacketType
import io.github.webbingon.glowserver.protocol.login.serverbound.ServerboundLoginPacketType
import io.github.webbingon.glowserver.protocol.status.clientbound.ClientboundStatusPacketType
import io.github.webbingon.glowserver.protocol.status.serverbound.ServerboundStatusPacketType
import kotlin.enums.enumEntries

object PacketTypeRegistry {
    private val registryMap = mutableMapOf<Triple<Int, ConnectionState, PacketDirection>, PacketType>()

    init {
        register<ClientboundConfigurationPacketType>()
        register<ClientboundLoginPacketType>()
        register<ClientboundStatusPacketType>()
        register<ServerboundConfigurationPacketType>()
        register<ServerboundHandshakePacketType>()
        register<ServerboundLoginPacketType>()
        register<ServerboundStatusPacketType>()
    }

    private inline fun <reified T> register() where T : Enum<T>, T : PacketType {
        for (entry in enumEntries<T>()) {
            val key = Triple(entry.id, entry.state, entry.direction)

            registryMap[key] = entry
        }
    }

    fun get(
        id: Int,
        state: ConnectionState,
        direction: PacketDirection,
    ): PacketType? {
        val key = Triple(id, state, direction)

        return registryMap[key]
    }
}
