@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

plugins {
    idea
    `java-library`
    alias(libs.plugins.moddev)
    alias(libs.plugins.modpublisher)
    alias(libs.plugins.dotenv)
}

val mcVersion = libs.versions.minecraft.get()
val curseforgeToken: String = env.fetch("CF_TOKEN", "").trim()
val modrinthToken: String = env.fetch("MODRINTH_TOKEN", "").trim()
val modChangelog = rootProject.file("CHANGELOG.md").readText().split("###")[1].let { x -> "###$x".trim() }

group = mod.group
version = "${mod.version}-$mcVersion"

base.archivesName.set("${mod.id}-neoforge")
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

neoForge {
    version = libs.versions.neoforge.get()

    parchment {
        minecraftVersion = mcVersion
        mappingsVersion = libs.versions.parchment.get()
    }

    runs {
        register("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod.id)
        }
        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod.id)
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.WARN
        }
    }

    mods {
        register(mod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

repositories {
    maven("https://jitpack.io") { name = "JitPack" }
    maven("https://api.modrinth.com/maven") { content { includeGroup("maven.modrinth") } }
    maven("https://maven.shedaniel.me/") // Cloth Config
    maven("https://maven.latvian.dev/releases") {
        // KubeJS and Rhino
        name = "latvian.dev Maven"
        content {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.latvian.apps")
        }
    }
}

dependencies {
    implementation(libs.norealmsbutton)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.kubejs)
    implementation(libs.clothconfig)
}

tasks {
    processResources {
        inputs.property("version", version)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = "UTF-8"

        from(file("LICENSE")) { rename { "LICENSE.txt" } }
        filesMatching("META-INF/neoforge.mods.toml") { expand("version" to version) }
        from(file("third-party-licenses")) { into("third-party-licenses") }
        from(file("assets/logo.png")) { rename { "${mod.id}_logo.png" } }
        from(file("assets/private-logo.png")) { rename { "${mod.id}_logo.png" } }
    }
    publisher {
        apiKeys {
            modrinth(modrinthToken)
            curseforge(curseforgeToken)
        }
        modrinthID.set(pub.modrinth_id)
        curseID.set(pub.curseforge_id)

        debug.set(pub.debug)

        artifact.set(jar)

        versionType.set(mod.release_type)
        changelog.set(modChangelog)
        displayName.set("${mod.name} ${mod.version} for NeoForge $mcVersion")
        projectVersion.set("${project.version}-neoforge")
        loaders.add("neoforge")
        gameVersions.addAll(mod.game_version_supports)

        modrinthDepends {
            required("kubejs")
            optional("cloth-config")
        }
        curseDepends {
            required("kubejs")
            optional("cloth-config")
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
