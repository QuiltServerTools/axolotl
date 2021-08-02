@file:OptIn(KordPreview::class)

package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.checks.memberFor
import com.kotlindiscord.kord.extensions.commands.slash.SlashCommand
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake

fun SlashCommand<*>.onlyModerator() {
    check {
        val member = memberFor(event)
        if (member == null) {
            fail("Not in a server!")
        } else {
            if (member.asMember().roleIds.none { it == MODERATOR_ROLE }) {
                fail("You don't have the right permissions")
            }
        }
    }

    // Discord says not to trust their role checks, so we have a manual check above too.
    allowRole(MODERATOR_ROLE)
}
