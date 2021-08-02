package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake

val SERVER_ID = Snowflake(
    env("SERVER_ID")?.toLong()
        ?: error("Env var SERVER_ID not provided"))

val TOKEN = env("BOT_TOKEN")
    ?: error("Env var BOT_TOKEN not provided")

val MODERATOR_ROLE = env("MODERATOR_ROLE")
    ?: error("Env var MODERATOR_ROLE not provided")

val TAG_PREFIX = env("TAG_PREFIX")
    ?: error("Env var TAG_PREFIX not provided")

val TAGS_DIR = env("TAGS_DIR") ?: "tags"
