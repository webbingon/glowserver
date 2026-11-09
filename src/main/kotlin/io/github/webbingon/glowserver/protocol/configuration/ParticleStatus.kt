package io.github.webbingon.glowserver.protocol.configuration

enum class ParticleStatus(
    val id: Int,
) {
    ALL(0),
    DECREASED(1),
    MINIMAL(2),
    ;

    companion object {
        private val map = entries.associateBy { it.id }

        fun fromId(id: Int): ParticleStatus? = map[id]
    }
}
