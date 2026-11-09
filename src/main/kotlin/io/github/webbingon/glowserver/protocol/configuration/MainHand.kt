package io.github.webbingon.glowserver.protocol.configuration

enum class MainHand(
    val id: Int,
) {
    LEFT(0),
    RIGHT(1),
    ;

    companion object {
        private val map = entries.associateBy { it.id }

        fun fromId(id: Int): MainHand? = map[id]
    }
}
