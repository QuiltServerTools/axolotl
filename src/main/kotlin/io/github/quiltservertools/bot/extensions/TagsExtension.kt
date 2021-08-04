@file:OptIn(KordPreview::class)

package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import io.github.quiltservertools.bot.SERVER_ID
import io.github.quiltservertools.bot.TAGS_DIR
import io.github.quiltservertools.bot.TAG_PREFIX
import io.github.quiltservertools.bot.onlyModerator
import io.github.quiltservertools.bot.tags.TagParser
import io.github.quiltservertools.bot.tags.applyFromTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import java.nio.file.Paths

class TagsExtension : Extension() {
    override val name = "tags"

    // Obtain the TagParser that was loaded in App.kt
    private val tagParser: TagParser by inject()

    override suspend fun setup() {
        event<MessageCreateEvent> {
            check { failIf(!event.message.content.startsWith(TAG_PREFIX)) }

            action {
                val pts = event.message.content.removePrefix(TAG_PREFIX).split("\\s".toRegex())
                val tagName = pts.first()
                val args = pts.drop(1)
                val tag = tagParser.getTag(tagName)

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
                val tagCount = withContext(Dispatchers.IO) {
                    tagParser.reloadTags(Paths.get(TAGS_DIR))
                }
                ephemeralFollowUp { content = "Loaded `$tagCount` tags!" }
            }
        }
    }
}
