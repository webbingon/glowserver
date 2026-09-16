package io.github.webbingon.glowserver.protocol.model

import kotlin.uuid.Uuid

data class GameProfile(
    val uuid: Uuid,
    val username: String,
    val properties: List<GameProfileProperty>,
)
