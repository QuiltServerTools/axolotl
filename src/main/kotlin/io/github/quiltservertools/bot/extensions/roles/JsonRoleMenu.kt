package io.github.quiltservertools.bot.extensions.roles

import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeText

class JsonRoleMenu : RoleMenuData {
    private val json = Json

    private val root = (envOrNull("DATA_ROOT") ?: "/data") + "/rolemenus"
    private val path = "$root/{id}.json"

    private val roleMenuCache: MutableMap<Snowflake, RoleMenu> = mutableMapOf()

    override fun get(id: Snowflake): RoleMenu? {
        if (roleMenuCache.containsKey(id)) {
            return roleMenuCache[id]
        }

        val file = getFile(id) ?: return null
        val suggestion = json.decodeFromString<RoleMenu>(file.readText())

        roleMenuCache[id] = suggestion

        return suggestion
    }

    override fun add(id: Snowflake, roleMenu: RoleMenu): Boolean {
        val existing = get(id)

        if (existing != null) {
            return false
        }

        roleMenuCache[id] = roleMenu
        save(id)

        return true
    }

    override fun load(): Int {
        if (roleMenuCache.isNotEmpty()) {
            save()
            roleMenuCache.clear()
        }

        val rootPath = Path.of(root)

        if (!rootPath.exists()) {
            rootPath.createDirectories()
        }

        return 0
    }

    override fun save(): Boolean =
        roleMenuCache.keys.map { id ->
            save(id)
        }.all { it }

    override fun save(id: Snowflake): Boolean {
        val suggestion = get(id) ?: return false
        val pathObj = Path.of(path.replace("{id}", id.asString))

        if (!pathObj.exists()) {
            pathObj.createFile()
        }

        pathObj.writeText(json.encodeToString(suggestion))

        return true
    }

    override fun save(roleMenu: RoleMenu): Boolean {
        val id = roleMenu.id

        if (roleMenuCache[id] == null) {
            roleMenuCache[id] = roleMenu
        }

        val pathObj = Path.of(path.replace("{id}", id.asString))

        if (!pathObj.exists()) {
            pathObj.createFile()
        }

        pathObj.writeText(json.encodeToString(roleMenu))

        return true
    }

    fun getFile(id: Snowflake): File? {
        val pathObj = Path.of(path.replace("{id}", id.asString))

        if (pathObj.exists()) {
            return pathObj.toFile()
        }

        return null
    }
}