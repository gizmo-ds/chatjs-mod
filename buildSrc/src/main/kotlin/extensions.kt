@file:Suppress("PropertyName")

import org.gradle.api.Project

val Project.mod: ModData get() = ModData(this)
val Project.pub: PubData get() = PubData(this)

@JvmInline
@Suppress("MemberVisibilityCanBePrivate")
value class ModData(private val project: Project) {
    fun prop(key: String): String = requireNotNull(project.findProperty(key)?.toString()) { "Property $key not set." }

    val id: String get() = prop("mod.id")
    val name: String get() = prop("mod.name")
    val version: String get() = prop("mod.version")
    val group: String get() = prop("mod.group")
    val release_type: String get() = prop("mod.release_type")
    val game_version_supports: List<String> get() = prop("mod.game_version_supports").split(",")
}

@JvmInline
@Suppress("MemberVisibilityCanBePrivate")
value class PubData(private val project: Project) {
    fun prop(key: String): String = requireNotNull(project.findProperty(key)?.toString()) { "Property $key not set." }

    val modrinth_id: String get() = prop("pub.modrinth_id")
    val curseforge_id: String get() = prop("pub.curseforge_id")
    val debug: Boolean get() = prop("pub.debug").toBoolean()
}
