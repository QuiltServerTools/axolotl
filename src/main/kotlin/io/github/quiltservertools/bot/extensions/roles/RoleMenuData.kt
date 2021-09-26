package io.github.quiltservertools.bot.extensions.roles

import dev.kord.common.entity.Snowflake

interface RoleMenuData {
    fun get(id: String): RoleMenu? = get(Snowflake(id))
    fun get(id: Snowflake): RoleMenu?

    fun add(id: String, roleMenu: RoleMenu): Boolean = add(Snowflake(id), roleMenu)
    fun add(id: Snowflake, roleMenu: RoleMenu): Boolean

    fun load(): Int

    fun save(): Boolean
    fun save(id: Snowflake): Boolean
    fun save(roleMenu: RoleMenu): Boolean
}