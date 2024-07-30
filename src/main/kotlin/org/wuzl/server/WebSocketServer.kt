package org.wuzl.server

import org.glassfish.tyrus.server.Server
import org.wuzl.server.endpoints.RealTimeChat

fun main() {
    val server = Server(RealTimeChat::class.java)
    server.start()
    println("Press ENTER to stop the server...")
    readln()
}