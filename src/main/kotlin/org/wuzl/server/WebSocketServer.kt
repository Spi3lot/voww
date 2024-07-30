package org.wuzl.server

import org.glassfish.tyrus.server.Server
import org.wuzl.server.endpoints.RealTimeChatEndpoint

fun main() {
    val server = Server(RealTimeChatEndpoint::class.java)
    server.start()
    println("Press ENTER to stop the server...")
    readln()
}