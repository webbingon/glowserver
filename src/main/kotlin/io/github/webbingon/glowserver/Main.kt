package io.github.webbingon.glowserver

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 25565

    val server = GlowServer(port)

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
    })

    server.run()
}
