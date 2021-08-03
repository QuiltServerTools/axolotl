package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.utils.loadModule
import io.github.quiltservertools.bot.extensions.SupportExtension
import io.github.quiltservertools.bot.extensions.TagsExtension
import io.github.quiltservertools.bot.tags.TagParser
import me.shedaniel.linkie.namespaces.YarnNamespace
import java.nio.file.Paths

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        messageCommands {
            defaultPrefix = "?"
        }

        slashCommands {
            enabled = true
        }

        extensions {
            add(::TagsExtension)
            add(::SupportExtension)

            extMappings {
                namespaceCheck { namespace -> {
                    failIfNot("Non-yarn commands can only be used in DM") {
                        namespace == YarnNamespace || event.guildId == null
                    }
                } }
            }
        }
    }

    // Provide the tag parser using koin injection
    val tagParser = TagParser()
    tagParser.loadTags(Paths.get(TAGS_DIR))
    loadModule { single { tagParser } }

    bot.start()
}