package org.wuzl.communication.data

import java.io.Serializable
import java.util.*

/**
 * @author Emilio Zottel (5AHIF)
 * @since 01.08.2024, Do.
 **/
class VoiceChannel(
    val uuid: UUID,
    private val name: String,
) : Serializable {

    companion object {

        private val channels = hashMapOf<UUID, VoiceChannel>(
            with(UUID.randomUUID()) { this to VoiceChannel(this, "EÖV") }
        )

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