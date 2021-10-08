package io.github.quiltservertools.bot

import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.common.entity.Snowflake

val SERVER_ID = Snowflake(env("SERVER_ID").toLong())

val TOKEN = env("BOT_TOKEN")

val MODERATOR_ROLE = Snowflake(env("MODERATOR_ROLE").toLong())

val TAG_PREFIX = env("TAG_PREFIX")

val REPO_URL = envOrNull("REPO_DIR") ?: "https://github.com/QuiltServerTools/axolotl"

val SUPPORT_CHANNEL = Snowflake(env("SUPPORT_CHANNEL").toLong())
