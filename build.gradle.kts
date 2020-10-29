
plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"

    application
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    val ktorVersion = "1.3.1"
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")

    implementation("org.snakeyaml:snakeyaml-engine:2.1")
    implementation("com.charleskorn.kaml:kaml:0.24.+")

    testImplementation("org.jetbrains.kotlin:kotlin-test")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}


tasks.jar {
    manifest {
        attributes["Main-Class"] = "me.maxklyukin.cashbot.AppKt"
    }
    from(configurations.compileClasspath.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}


application {
    mainClassName = "me.maxklyukin.cashbot.AppKt"
}