plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.panda-lang.org/releases/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    // implementation("dev.rollczi:litecommands-velocity:3.0.0-BETA-pre15") // <-- uncomment in your project
    // implementation("dev.rollczi:litecommands-framework:3.0.0-BETA-pre15") // <-- uncomment in your project
    implementation(project(":litecommands-velocity")) // don't use this line in your build.gradle
    implementation(project(":litecommands-framework")) // don't use this line in your build.gradle

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("ExamplePlugin v${project.version}.jar")

    relocate("panda", "dev.rollczi.example.bukkit.libs.org.panda")
    relocate("org.panda_lang", "dev.rollczi.example.bukkit.libs.org.panda")
    relocate("dev.rollczi.litecommands", "dev.rollczi.example.bukkit.libs.dev.rollczi")
}

sourceSets {
    main {
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}

