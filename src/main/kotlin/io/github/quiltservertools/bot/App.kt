package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.loadModule
import dev.kord.common.entity.Snowflake
import io.github.quiltservertools.bot.extensions.TagsExtension
import io.github.quiltservertools.bot.tags.TagParser
import me.shedaniel.linkie.namespaces.YarnNamespace
import java.nio.file.Paths

val SERVER_ID = Snowflake(
    env("SERVER_ID")?.toLong()  // Get the test server ID from the env vars or a .env file
        ?: error("Env var SERVER_ID not provided")
)

private val TOKEN = env("BOT_TOKEN")   // Get the bot' token from the env vars or a .env file
    ?: error("Env var BOT_TOKEN not provided")

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
    tagParser.loadTags(Paths.get("tags"))
    loadModule { single { tagParser } }

    bot.start()
}
