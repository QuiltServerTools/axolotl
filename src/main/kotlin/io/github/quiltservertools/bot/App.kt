package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.utils.loadModule
import io.github.quiltservertools.bot.extensions.RoleMenuExtension
import io.github.quiltservertools.bot.extensions.SupportExtension
import io.github.quiltservertools.bot.extensions.TagsExtension
import io.github.quiltservertools.bot.tags.TagRepo
import me.shedaniel.linkie.namespaces.YarnNamespace
import java.nio.file.Paths

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        chatCommands {
            enabled = true
            defaultPrefix = "?"
        }

        applicationCommands {
            enabled = true
        }

        extensions {
            add(::TagsExtension)
            add(::SupportExtension)
            add(::RoleMenuExtension)

            extMappings {
                namespaceCheck { namespace ->
                    {
                        failIfNot("Non-yarn commands can only be used in DM") {
                            namespace == YarnNamespace || event.guildId == null
                        }
                    }
                }
            }
        }
    }

    // Provide the tag parser using koin injection
    val tagRepo = TagRepo(Paths.get("tags-repo"))
    tagRepo.init()

    loadModule {
        single { tagRepo }
    }

    bot.start()
}
