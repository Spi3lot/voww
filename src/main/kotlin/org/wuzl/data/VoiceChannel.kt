package org.wuzl.data

import java.util.*

/**
 * @author Emilio Zottel (5AHIF)
 * @since 01.08.2024, Do.
 **/
class VoiceChannel(
    val uuid: UUID,
    val name: String,
) {

    companion object {

        private val channels = hashMapOf<UUID, VoiceChannel>()

        operator fun get(uuid: UUID) = channels[uuid]

    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VoiceChannel
        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }


}