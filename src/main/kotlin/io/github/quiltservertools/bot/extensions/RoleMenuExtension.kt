package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.checks.hasRole
import com.kotlindiscord.kord.extensions.checks.inGuild
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.commands.converters.impl.roleList
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.stringList
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.publicSelectMenu
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.ackEphemeral
import com.kotlindiscord.kord.extensions.utils.emoji
import com.kotlindiscord.kord.extensions.utils.hasRole
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.message.create.embed
import io.github.quiltservertools.bot.MODERATOR_ROLE
import io.github.quiltservertools.bot.SERVER_ID

class RoleMenuExtension : Extension() {
    override val name = "role-menu"


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
                            this.id
                            maximumChoices = arguments.maxSelections
                            minimumChoices = 0
                            arguments.roles.forEachIndexed { index, role ->
                                option(role.name, role.id.toString()) {
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

                val allowedRoles = interaction.component.options
                    .mapNotNull { option ->
                        option.value.toLongOrNull()?.let { Snowflake(it) }
                    }

                val member = guild.getMember(event.interaction.user.id)

                val response = interaction.ackEphemeral()
                var responseContent = ""
                for (roleID in allowedRoles) {
                    val role = guild.getRoleOrNull(roleID) ?: continue
                    if (interaction.values.contains(roleID.toString())) {
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

                response.createEphemeralFollowup {
                    content = responseContent.ifEmpty { "No changes" }
                }
            }
        }
    }

    inner class RoleMenuArgs : Arguments() {
        val title by string {
            name = "Title"
            description = "Title for the embed"
        }
        val description by string {
            name = "Description"
            description = "Description for the embed"
        }
        val maxSelections by int {
            name = "Max Selections"
            description = "Maximum amount of roles that can be selected"
        }
        val roles by roleList {
            name = "Roles"
            description = "The roles that can be selected"
        }
        val emoji by stringList {
            name = "Emojis"
            description = "The unicode emojis to go with the selected roles"
        }
    }
}
