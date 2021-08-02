@file:OptIn(KordPreview::class)

package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.checks.memberFor
import com.kotlindiscord.kord.extensions.commands.slash.SlashCommand
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake

fun SlashCommand<*>.onlyModerator() {
    allowRole(MODERATOR_ROLE)
}
