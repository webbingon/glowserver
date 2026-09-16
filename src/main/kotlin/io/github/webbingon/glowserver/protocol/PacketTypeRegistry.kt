package io.github.webbingon.glowserver.protocol

import io.github.webbingon.glowserver.protocol.clientbound.ClientboundConfigurationPacketType
import io.github.webbingon.glowserver.protocol.clientbound.ClientboundLoginPacketType
import io.github.webbingon.glowserver.protocol.clientbound.ClientboundStatusPacketType
import io.github.webbingon.glowserver.protocol.serverbound.ServerboundConfigurationPacketType
import io.github.webbingon.glowserver.protocol.serverbound.ServerboundHandshakePacketType
import io.github.webbingon.glowserver.protocol.serverbound.ServerboundLoginPacketType
import io.github.webbingon.glowserver.protocol.serverbound.ServerboundStatusPacketType
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
