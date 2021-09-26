package io.github.quiltservertools.bot.extensions.roles

import com.kotlindiscord.kord.extensions.checks.hasRole
import com.kotlindiscord.kord.extensions.checks.inGuild
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.*
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.publicSelectMenu
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicMessageCommand
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optional
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.component.SelectOptionBuilder
import dev.kord.rest.builder.message.create.embed
import io.github.quiltservertools.bot.MODERATOR_ROLE
import io.github.quiltservertools.bot.SERVER_ID
import org.koin.core.component.inject
import java.awt.SystemColor.menu
import java.util.*
import kotlin.time.ExperimentalTime

@OptIn(KordPreview::class, ExperimentalTime::class)
class RoleMenuExtension : Extension() {
    override val name = "role-menu"

    private val roleMenus: RoleMenuData by inject()

    @OptIn(KordPreview::class)
    override suspend fun setup() {
        chatCommand(::RoleMenuArgs) {
            name = "create-role-menu"
            description = "Creates a role menu"

            check { inGuild(SERVER_ID)}
            check { hasRole(MODERATOR_ROLE) }

            action {
                this.channel.createMessage {
                    embed {
                        title = arguments.title
                        description = arguments.description
                    }

                    components {
                        publicSelectMenu {
                            maximumChoices = arguments.maxSelections
                            arguments.roles.forEachIndexed { index, role ->
                                option(role.name, role.id.asString) {
                                    if (arguments.emoji.size > index) {
                                        emoji(ReactionEmoji.from(arguments.emoji[index]))
                                    }
                                }
                            }

                            action {

                            }
                        }
                    }
                }
            }
        }

        event<InteractionCreateEvent> {
            check { inGuild(SERVER_ID) }
            check { failIfNot(event.interaction is SelectMenuInteraction) }

            action {
                val interaction = event.interaction as SelectMenuInteraction
                val guild = kord.getGuild(interaction.data.guildId.value!!)!!

                val message = interaction.message ?: return@action
                val allowedRoles = interaction.component!!.options
                    .mapNotNull { option ->
                        option.value.toLongOrNull()?.let { Snowflake(it) }
                    }

                val member = guild.getMember(event.interaction.user.id)

                for (selection in interaction.values) {
                    if (selection.toLongOrNull() == null) return@action
                    val role = guild.getRoleOrNull(Snowflake(selection)) ?: return@action
                    if (!allowedRoles.contains(role.id)) return@action

                    member.addRole(role.id, "Role Menu Selection")
                }
            }
        }
    }

    inner class RoleMenuArgs : Arguments() {
        val title by string("Title", "Title for the embed")
        val description by string("Description", "Description for the embed")
        val maxSelections by int("Max Selections", "Maximum amount of roles that can be selected")
        val roles by roleList("Roles", "The roles that can be selected")
        val emoji by emojiList("Emojis", "The emojis to go with the selected roles")
    }
}

@OptIn(KordPreview::class)
fun SelectOptionBuilder.emoji(emoji: ReactionEmoji): Unit = when (emoji) {
    is ReactionEmoji.Unicode -> emoji(emoji)
    is ReactionEmoji.Custom -> emoji(emoji)
}

@OptIn(KordPreview::class)
fun SelectOptionBuilder.emoji(unicodeEmoji: ReactionEmoji.Unicode) {
    emoji = DiscordPartialEmoji(
        name = unicodeEmoji.name
    )
}

@OptIn(KordPreview::class)
        /** Convenience function for setting [partialEmoji] based on a given reaction emoji. **/
fun SelectOptionBuilder.emoji(guildEmoji: ReactionEmoji.Custom) {
    emoji = DiscordPartialEmoji(
        id = guildEmoji.id,
        name = guildEmoji.name,
        animated = guildEmoji.isAnimated.optional()
    )
}
