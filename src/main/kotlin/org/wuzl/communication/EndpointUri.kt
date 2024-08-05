package org.wuzl.communication

import java.net.URI

/**
 * @author Emilio Zottel
 * @since 05.08.2024, Mo.
 **/
object EndpointUri {

    private const val PREFIX = "ws://localhost:8025"

    const val HUB = "hub"

    const val RTC = "rtc"

    fun absolute(path: String): URI {
        return URI.create("$PREFIX/$path")
    }

}