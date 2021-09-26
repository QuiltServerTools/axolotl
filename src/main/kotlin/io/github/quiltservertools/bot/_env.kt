package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.common.entity.Snowflake

val SERVER_ID = Snowflake(
    envOrNull("SERVER_ID")?.toLong()
        ?: error("Env var SERVER_ID not provided")
)

val TOKEN = envOrNull("BOT_TOKEN")
    ?: error("Env var BOT_TOKEN not provided")

val MODERATOR_ROLE = Snowflake(
    envOrNull("MODERATOR_ROLE")?.toLong()
        ?: error("Env var MODERATOR_ROLE not provided")
)

val TAG_PREFIX = envOrNull("TAG_PREFIX")
    ?: error("Env var TAG_PREFIX not provided")

val REPO_URL = envOrNull("REPO_DIR") ?: "https://github.com/QuiltServerTools/axolotl"

val SUPPORT_CHANNEL = Snowflake(
    envOrNull("SUPPORT_CHANNEL")?.toLong()
        ?: error("Env var SUPPORT_CHANNEL not provided")
)
