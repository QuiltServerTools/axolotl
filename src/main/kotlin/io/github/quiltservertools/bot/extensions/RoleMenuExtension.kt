package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.checks.hasRole
import com.kotlindiscord.kord.extensions.checks.inGuild
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.*
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.publicSelectMenu
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.ackEphemeral
import com.kotlindiscord.kord.extensions.utils.emoji
import com.kotlindiscord.kord.extensions.utils.hasRole
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.followUpEphemeral
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.message.create.embed
import io.github.quiltservertools.bot.MODERATOR_ROLE
import io.github.quiltservertools.bot.SERVER_ID
import kotlin.time.ExperimentalTime

@OptIn(KordPreview::class, ExperimentalTime::class)
class RoleMenuExtension : Extension() {
    override val name = "role-menu"


    @OptIn(KordPreview::class)
    override suspend fun setup() {
        chatCommand(::RoleMenuArgs) {
            name = "create-role-menu"
            description = "Creates a role menu"

            check { inGuild(SERVER_ID) }
            check { hasRole(MODERATOR_ROLE) }

            action {
                this.channel.createMessage {
                    embed {
                        title = arguments.title
                        description = arguments.description
                    }

                    val components = components {
                        publicSelectMenu {
                            maximumChoices = arguments.maxSelections
                            minimumChoices = 0
                            arguments.roles.forEachIndexed { index, role ->
                                option(role.name, role.id.asString) {
                                    if (arguments.emoji.size > index) {
                                        emoji(arguments.emoji[index])
                                    }
                                }
                            }

                            action {

                            }
                        }

                    }
                    // Unregister the built-in listener for the unused action callback above
                    components.removeAll()
                }
            }
        }

        event<InteractionCreateEvent> {
            check { inGuild(SERVER_ID) }
            check { failIfNot(event.interaction is SelectMenuInteraction) }

            action {
                val interaction = event.interaction as SelectMenuInteraction
                val guild = kord.getGuild(interaction.data.guildId.value!!)!!

                val allowedRoles = interaction.component!!.options
                    .mapNotNull { option ->
                        option.value.toLongOrNull()?.let { Snowflake(it) }
                    }

                val member = guild.getMember(event.interaction.user.id)

                var responseContent = ""
                for (roleID in allowedRoles) {
                    val role = guild.getRoleOrNull(roleID) ?: continue
                    if (interaction.values.contains(roleID.asString)) {
                        if (!member.hasRole(role)) {
                            member.addRole(roleID, "Role Menu selection")
                            responseContent += "Added ${role.mention}\n"
                        }
                    } else {
                        if (member.hasRole(role)) {
                            member.removeRole(roleID, "Role Menu selection")
                            responseContent += "Removed ${role.mention}\n"
                        }
                    }
                }

                val response = interaction.ackEphemeral()
                response.followUpEphemeral {
                    content = responseContent.ifEmpty { "No changes" }
                }
            }
        }
    }

    inner class RoleMenuArgs : Arguments() {
        val title by string("Title", "Title for the embed")
        val description by string("Description", "Description for the embed")
        val maxSelections by int("Max Selections", "Maximum amount of roles that can be selected")
        val roles by roleList("Roles", "The roles that can be selected")
        val emoji by stringList("Emojis", "The unicode emojis to go with the selected roles")
    }
}
