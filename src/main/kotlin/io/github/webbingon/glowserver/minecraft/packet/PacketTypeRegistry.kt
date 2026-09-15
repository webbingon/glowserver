package io.github.webbingon.glowserver.minecraft.packet

import io.github.webbingon.glowserver.minecraft.ConnectionState
import io.github.webbingon.glowserver.minecraft.packet.clientbound.ClientboundLoginPacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundHandshakePacketType
import io.github.webbingon.glowserver.minecraft.packet.serverbound.ServerboundLoginPacketType
import kotlin.enums.enumEntries

object PacketTypeRegistry {
    private val registryMap = mutableMapOf<Triple<Int, ConnectionState, PacketDirection>, PacketType>()

    init {
        register<ClientboundLoginPacketType>()
        register<ServerboundHandshakePacketType>()
        register<ServerboundLoginPacketType>()
    }

    private inline fun <reified T> register() where T : Enum<T>, T : PacketType {
        for (entry in enumEntries<T>()) {
            val key = Triple(entry.id, entry.state, entry.direction)

            registryMap[key] = entry
        }
    }

    fun get(id: Int, state: ConnectionState, direction: PacketDirection) : PacketType? {
        val key = Triple(id, state, direction)

        return registryMap[key]
    }
}
