package io.github.webbingon.glowserver.protocol.configuration

enum class ChatMode(
    val id: Int,
) {
    ENABLED(0),
    COMMANDS_ONLY(1),
    HIDDEN(2),
    ;

    companion object {
        private val map = entries.associateBy { it.id }

        fun fromId(id: Int): ChatMode? = map[id]
    }
}
