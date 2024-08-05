package org.wuzl.server

import org.glassfish.tyrus.server.Server
import org.wuzl.server.endpoints.Hub
import org.wuzl.server.endpoints.RealTimeChat

fun main() {
    val server = Server(Hub::class.java, RealTimeChat::class.java)
    server.start()
    println("Press ENTER to stop the server...")
    readln()
}