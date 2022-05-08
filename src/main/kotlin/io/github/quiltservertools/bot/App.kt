package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.utils.loadModule
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.quiltservertools.bot.extensions.RoleMenuExtension
import io.github.quiltservertools.bot.extensions.SupportExtension
import io.github.quiltservertools.bot.extensions.TagsExtension
import io.github.quiltservertools.bot.tags.TagRepo
import java.nio.file.Paths

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        chatCommands {
            enabled = true
            defaultPrefix = "?"
        }

        applicationCommands {
            enabled = true
            syncPermissions = false
        }

        extensions {
            add(::TagsExtension)
            add(::SupportExtension)
            add(::RoleMenuExtension)

            extMappings { }
        }

        intents {
            +Intent.GuildMembers
        }

        members {
            all()
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
