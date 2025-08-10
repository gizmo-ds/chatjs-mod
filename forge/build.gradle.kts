@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    alias(libs.plugins.shadow)
}

apply(plugin = "com.modrinth.minotaur")
apply(plugin = "net.darkhax.curseforgegradle")

loom {
    forge {
        mixinConfig("chatjs-common.mixins.json")
        mixinConfig("chatjs.mixins.json")
    }
}

architectury { forge() }

val shadowBundle: Configuration by configurations.getting
val developmentForge: Configuration by configurations.getting
configurations {
    developmentForge.extendsFrom(common.get())
}

repositories {
}

dependencies {
    forge(libs.forge.forge)

    localRuntime(libs.mixinextras.forge)
    modLocalRuntime(libs.norealmsbutton.forge)

    modApi(libs.architectury.forge)
    modImplementation(libs.kubejs.forge)
    modApi(libs.clothconfig.forge)
//    modCompileOnly(libs.clothconfig.forge)

    shadowBundle(project(path = ":common", configuration = "transformProductionForge"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
        from(rootProject.file("assets/logo.png")) {
            rename { "${mod.id}-logo.png" }
        }
    }

    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier.set("dev-shadow")

        mergeServiceFiles()
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
    }

    if (mod.modrinth_id.isNotEmpty() && (ext.get("modrinth_token") as String).isNotEmpty())
        modrinth { uploadFile.set(remapJar.flatMap { it.archiveFile }) }
    if (mod.curseforge_id.isNotEmpty() && (ext.get("curseforge_token") as String).isNotEmpty())
        curseforge {
            val mainFile = upload(mod.curseforge_id, remapJar.flatMap { it.archiveFile })
            mainFile.releaseType = mod.release_type
            mainFile.gameVersions.addAll(mod.game_version_supports)
            mainFile.addModLoader(project.name)
            mainFile.changelog = ext.get("changelog")
            mainFile.addEnvironment("Server", "Client")
            mainFile.addRequirement("kubejs")
            mainFile.addOptional("cloth-config")
        }
}