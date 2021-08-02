package io.github.quiltservertools.bot

import dev.kord.common.entity.ArchiveDuration
import dev.kord.core.entity.Guild

// Borrowed from https://github.com/QuiltMC/cozy-discord/blob/root/src/main/kotlin/org/quiltmc/community/_Utils.kt#L117
fun Guild.getMaxArchiveDuration(): ArchiveDuration {
    val features = features.filter {
        it.value == "THREE_DAY_THREAD_ARCHIVE" ||
                it.value == "SEVEN_DAY_THREAD_ARCHIVE"
    }.map { it.value }

    return when {
        features.contains("SEVEN_DAY_THREAD_ARCHIVE") -> ArchiveDuration.Week
        features.contains("THREE_DAY_THREAD_ARCHIVE") -> ArchiveDuration.ThreeDays

        else -> ArchiveDuration.Day
    }
}
