@file:OptIn(KordPreview::class)

package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import io.github.quiltservertools.bot.SERVER_ID
import io.github.quiltservertools.bot.TAG_PREFIX
import io.github.quiltservertools.bot.onlyModerator
import io.github.quiltservertools.bot.tags.TagRepo
import io.github.quiltservertools.bot.tags.applyFromTag
import org.koin.core.component.inject

class TagsExtension : Extension() {
    override val name = "tags"

    // Obtain the TagRepo that was loaded in App.kt
    private val tagRepo: TagRepo by inject()

    override suspend fun setup() {
        event<ReadyEvent> {
            action {
                tagRepo.reload()
            }
        }

        event<MessageCreateEvent> {
            check { failIf(!event.message.content.startsWith(TAG_PREFIX)) }

            action {
                val pts = event.message.content.removePrefix(TAG_PREFIX).split("\\s".toRegex())
                val tagName = pts.first()
                val args = pts.drop(1)
                val tag = tagRepo[tagName]

                if (tag != null) {
                    event.message.channel.createMessage {
                        applyFromTag(kord, tag, args)
                    }
                }
            }
        }

        slashCommand {
            name = "reload-tags"
            description = "Reloads the tags"

            onlyModerator()
            guild(SERVER_ID)

            autoAck = AutoAckType.EPHEMERAL

            action {
                val tagCount = tagRepo.reload()
                ephemeralFollowUp { content = "Loaded `$tagCount` tags!" }
            }
        }
    }
}
