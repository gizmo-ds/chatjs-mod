architectury {
    common(mod.enabled_platforms)
}

dependencies {
    modImplementation(libs.fabric.loader)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    modApi(libs.architectury.api)
    modImplementation(libs.kubejs.api)
    implementation(libs.nightconfig)
    modImplementation(libs.clothconfig.api)
}

tasks.test {
    useJUnitPlatform()

    if (!gradle.startParameter.taskNames.contains("build")) {
        arrayOf("API_KEY", "PROVIDER", "PROVIDER_MODEL", "PROVIDER_BASEURL")
            .forEach { environment(it, env.fetch(it, "")) }
    }
}
