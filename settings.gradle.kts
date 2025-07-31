pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://maven.firstdark.dev/releases")
        gradlePluginPortal()
    }
}

rootProject.name = "chatjs"

include("common", "fabric", "forge")
