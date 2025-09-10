@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    alias(libs.plugins.shadow)
}

apply(plugin = "com.hypherionmc.modutils.modpublisher")

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
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"

        filesMatching("META-INF/mods.toml") { expand("version" to project.version) }
    }

    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
    }

    publisher {
        artifact.set(remapJar)
    }
}