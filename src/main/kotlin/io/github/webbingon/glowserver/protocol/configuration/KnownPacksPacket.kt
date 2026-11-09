package io.github.webbingon.glowserver.protocol.configuration

import io.github.webbingon.glowserver.protocol.common.Packet
import io.github.webbingon.glowserver.protocol.model.KnownPack

interface KnownPacksPacket : Packet {
    val knownPacks: List<KnownPack>
}
