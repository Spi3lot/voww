package org.wuzl.communication.data

import java.io.Serializable

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
class SessionData(
    val id: String,
    val data: ByteArray,
) : Serializable