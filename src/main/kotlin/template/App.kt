/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package template

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import template.extensions.TestExtension
import template.extensions.CommandTest

val TEST_SERVER_ID = Snowflake(
    env("TEST_SERVER")?.toLong()  // Get the test server ID from the env vars or a .env file
        ?: error("Env var TEST_SERVER not provided")
)

private val TOKEN = env("TOKEN")   // Get the bot' token from the env vars or a .env file
    ?: error("Env var TOKEN not provided")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        messageCommands {
            defaultPrefix = "?"

            prefix { default ->
                if (guildId == TEST_SERVER_ID) {
                    // For the test server, we use ! as the command prefix
                    "!"
                } else {
                    // For other servers, we use the configured default prefix
                    default
                }
            }
        }

        slashCommands {
            enabled = true
        }

        extensions {
            add(::TestExtension)
            add(::CommandTest)
        }
    }

    bot.start()
}