@file:OptIn(KordPreview::class)

package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.commands.application.slash.SlashCommand
import dev.kord.common.annotation.KordPreview

fun SlashCommand<*, *>.onlyModerator() {
    allowRole(MODERATOR_ROLE)
}
