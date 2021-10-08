package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.DiscordRelayedException
import com.kotlindiscord.kord.extensions.checks.isInThread
import com.kotlindiscord.kord.extensions.checks.memberFor
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingBoolean
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.channel.threads.edit
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.message.MessageCreateEvent
import io.github.quiltservertools.bot.*

/**
 * Automatically opens new threads when messages are posted in the support channel.
 * Also adds /rename-thread and /archive helper commands
 */
@OptIn(KordPreview::class)
class SupportExtension : Extension() {
    override val name = "support"

    override suspend fun setup() {
        event<MessageCreateEvent> {
            check {
                failIfNot(event.message.channelId == SUPPORT_CHANNEL)
                failIf(event.message.getAuthorAsMember()?.roleIds?.any { it == MODERATOR_ROLE } != false)
            }

            action {
                val thread =
                    (event.message.channel.asChannel() as TextChannel).startPublicThreadWithMessage(
                        messageId = event.message.id,
                        name = event.message.author?.tag?.channelify() ?: "Support",
                        archiveDuration = event.message.getGuild().getMaxArchiveDuration(),
                    )
                thread.leave()
            }
        }

        publicSlashCommand(::RenameArgs) {
            name = "rename-thread"
            description = "Rename the current thread, if you have permission to do so."

            guild(SERVER_ID)

            check {
                // TODO: Replace this with threadFor when it works with InteractionCreateEvents
                val thread = event.interaction.channel.asChannelOrNull() as? ThreadChannel
                if (thread == null) {
                    fail(message = "Must be run in a thread!")
                    return@check
                }
                val member = memberFor(event)
                if (member == null) {
                    fail(message = "Must be run in a server!")
                    return@check
                }
                failIf("Can only be run in threads in <#$SUPPORT_CHANNEL>") {
                    thread.parentId != SUPPORT_CHANNEL
                }
                failIf("This thread is not yours or has already been renamed!") {
                    thread.asChannel().name != member.asUser().tag.channelify()
                }
                // Force pass if user is moderator
                if (member.asMember().isModerator()) pass()
            }

            action {
                val thread = channel.asChannel() as ThreadChannel
                val newName = arguments.name.channelify()
                thread.edit {
                    name = newName
                }
                // Additional name sanitization needed here to prevent breaking out of the monospaced block.
                respond { content = "Renamed this thread to `${newName.replace("`", "")}`" }
            }
        }

        ephemeralSlashCommand(::ArchiveArgs) {
            name = "archive"
            description = "Archives the current thread"
            guild(SERVER_ID)

            onlyModerator()
            check { isInThread() }

            action {
                val thread = channel.asChannel() as ThreadChannel
                thread.edit {
                    archived = true
                    locked = arguments.lock
                }
                respond {
                    content = if (arguments.lock) {
                        "Archived and locked the thread"
                    } else {
                        "Archived the thread"
                    }
                }
            }
        }
    }

    class RenameArgs : Arguments() {
        val name by string(
            displayName = "name",
            description = "The new name for this thread",
            validator = { _, value ->
                if (value.length > 100) {
                    throw DiscordRelayedException("Name cannot be longer than 100 characters")
                }
            }
        )
    }

    class ArchiveArgs : Arguments() {
        val lock by defaultingBoolean(
            displayName = "lock",
            description = "Whether the thread should also be locked (defaults to false)",
            defaultValue = false,
        )
    }

    /**
     * Converts a [String] to a Discord channel name friendly [String]
     */
    private fun String.channelify(): String {
        val tmp = this.replace("#", "-")
        return if (tmp.length > 100) {
            tmp.substring(0, 100 - 3) + "..."
        } else {
            tmp
        }
    }
}
