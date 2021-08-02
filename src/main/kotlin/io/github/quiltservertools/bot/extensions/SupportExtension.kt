package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import io.github.quiltservertools.bot.MODERATOR_ROLE
import io.github.quiltservertools.bot.SUPPORT_CHANNEL
import io.github.quiltservertools.bot.getMaxArchiveDuration

/**
 * Currently only opens new threads when messages are posted in the support channel,
 * more features may be added later.
 */
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
                        name = event.message.author?.tag?.replace("#", "-") ?: "Support",
                        archiveDuration = event.message.getGuild().getMaxArchiveDuration(),
                    )
                thread.leave()
            }
        }
    }
}
