package io.github.webbingon.glowserver.minecraft.packet

import kotlinx.serialization.Serializable

@Serializable
data class ServerStatus(
    val version: Version,
    val players: Players? = null,
    val description: Description? = null,
    val favicon: String? = null,
    val enforcesSecureChat: Boolean? = null
) {
    @Serializable
    data class Version(val name: String, val protocol: Int)

    @Serializable
    data class Players(val max: Int, val online: Int, val sample: List<Sample>? = null) {
        @Serializable
        data class Sample(val name: String, val id: String)
    }

    @Serializable
    data class Description(val text: String)
}
