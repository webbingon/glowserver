package io.github.webbingon.glowserver.minecraft

import kotlin.uuid.Uuid

class GameProfile(
    val uuid: Uuid,
    val username: String,
    val properties: List<GameProfileProperty>,
)
