package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import io.github.quiltservertools.bot.SERVER_ID
import io.github.quiltservertools.bot.TAG_PREFIX
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
                    try {
                        event.message.channel.createMessage {
                            applyFromTag(kord, tag, args)
                        }
                    } catch (e: Exception) {
                        event.message.channel.createMessage {
                            content = "Failed to send that tag: `${e::class.java.simpleName}: ${e.message}`"
                        }
                    }
                }
            }
        }

        ephemeralSlashCommand {
            name = "reload-tags"
            description = "Reloads the tags"

            guild(SERVER_ID)

            action {
                val tagCount = tagRepo.reload()
                respond { content = "Loaded `$tagCount` tags!" }
            }
        }
    }
}
