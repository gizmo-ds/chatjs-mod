@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    alias(libs.plugins.shadow)
}

apply(plugin = "com.hypherionmc.modutils.modpublisher")

architectury { fabric() }

val shadowBundle: Configuration by configurations.getting
val developmentFabric: Configuration by configurations.getting
configurations {
    developmentFabric.extendsFrom(common.get())
}

repositories {
    // ModMenu
    maven("https://maven.terraformersmc.com/releases/") {
        name = "Terraformers"
        content { includeGroup("com.terraformersmc") }
    }
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
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") { expand("version" to project.version) }
    }

    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier.set("dev-shadow")

        relocate("com.electronwill.nightconfig", "${mod.group}.libs.nightconfig")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
    }

    publisher {
        artifact.set(remapJar)
    }
}