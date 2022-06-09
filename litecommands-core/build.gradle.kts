dependencies {
    api("org.panda-lang:expressible:1.1.20")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

val coreArtifact: String by rootProject.extra

publishing {
    publications {
        create<MavenPublication>("maven") {
            this.artifactId = coreArtifact
            this.from(components["java"])
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}