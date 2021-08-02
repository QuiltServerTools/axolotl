@file:OptIn(KordPreview::class)

package io.github.quiltservertools.bot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import io.github.quiltservertools.bot.tags.TagParser
import io.github.quiltservertools.bot.tags.applyFromTag
import org.koin.core.component.inject

val TAG_PREFIX = env("TAG_PREFIX") ?: error("Missing environment variable TAG_PREFIX")

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

                if (tag == null) {
                    event.message.reply { content = "Unknown tag called $tagName" }
                } else {
                    event.message.channel.createMessage {
                        applyFromTag(kord, tag, args)
                    }
                }
            }
        }
    }
}
