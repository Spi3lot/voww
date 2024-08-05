package org.wuzl.server

import jakarta.websocket.Session

/**
 * @author Emilio Zottel
 * @since 05.08.2024, Mo.
 **/
object TrafficManager {

    fun broadcast(
        obj: Any,
        sessions: HashSet<Session>,
        sourceSession: Session? = null,
    ) {
        sessions.asSequence()
            .filter { it != sourceSession }
            .forEach {
                if (it.isOpen) {
                    it.asyncRemote.sendObject(obj)
                } else {
                    sessions.remove(it)
                }
            }
    }

}