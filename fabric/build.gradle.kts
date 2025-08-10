@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    alias(libs.plugins.shadow)
}

apply(plugin = "com.modrinth.minotaur")
apply(plugin = "net.darkhax.curseforgegradle")

architectury { fabric() }

val shadowBundle: Configuration by configurations.getting
val developmentFabric: Configuration by configurations.getting
configurations {
    developmentFabric.extendsFrom(common.get())
}

repositories {
    maven("https://maven.terraformersmc.com/") { name = "Terraformers" } // ModMenu
}

dependencies {
    modImplementation(libs.fabric.loader)

    modImplementation(libs.modmenu)
    localRuntime(libs.mixinextras.fabric)
    modLocalRuntime(libs.norealmsbutton.fabric)

    implementation(libs.nightconfig)
    shadowBundle(libs.nightconfig)

    modApi(libs.fabric.api)
    modApi(libs.architectury.fabric)
    modImplementation(libs.kubejs.fabric)
    modApi(libs.clothconfig.fabric)
//    modCompileOnly(libs.clothconfig.fabric)

    shadowBundle(project(path = ":common", configuration = "transformProductionFabric"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
        from(rootProject.file("assets/logo.png")) {
            rename { "assets/${mod.id}/icon.png" }
        }
    }

    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier.set("dev-shadow")

        mergeServiceFiles()

        relocate("com.electronwill.nightconfig", "${mod.group}.libs.nightconfig")
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