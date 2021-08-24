package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake

val SERVER_ID = Snowflake(
    env("SERVER_ID")?.toLong()
        ?: error("Env var SERVER_ID not provided"))

val TOKEN = env("BOT_TOKEN")
    ?: error("Env var BOT_TOKEN not provided")

val MODERATOR_ROLE = Snowflake(
    env("MODERATOR_ROLE")?.toLong()
        ?: error("Env var MODERATOR_ROLE not provided"))

val TAG_PREFIX = env("TAG_PREFIX")
    ?: error("Env var TAG_PREFIX not provided")

val REPO_URL = env("REPO_DIR") ?: "https://github.com/QuiltServerTools/axolotl"

val SUPPORT_CHANNEL = Snowflake(
    env("SUPPORT_CHANNEL")?.toLong()
        ?: error("Env var SUPPORT_CHANNEL not provided"))
