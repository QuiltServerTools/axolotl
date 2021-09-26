package io.github.quiltservertools.bot.extensions.roles

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class RoleMenu(
    val id: Snowflake,
    val roles: Set<Snowflake>
) {
}