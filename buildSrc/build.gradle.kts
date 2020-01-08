import java.io.PrintWriter

plugins {
    java
    maven
}

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("software.amazon.smithy:smithy-model:0.9.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}

val schemataDir = "$projectDir/src/main/resources/META-INF/elastic-common-schema";
tasks.register<Copy>("copyEcsSchemaFiles") {
    from("$projectDir/ecs/schemas")
    into(schemataDir)

    doLast {
        val ecsManifestWriter = PrintWriter("$schemataDir/manifest")

        file(schemataDir).walk()
                .map(File::getName)
                .filter { it.endsWith(".yml") }
                .sorted()
                .forEach {
                    ecsManifestWriter.append("$it\n")
                }

        ecsManifestWriter.close()
    }
}

tasks.assemble {
    dependsOn("copyEcsSchemaFiles")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Use Junit5's test runner.
tasks.withType<Test> {
    useJUnitPlatform()
}
