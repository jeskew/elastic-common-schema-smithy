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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Use Junit5's test runner.
tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("software.amazon.smithy:smithy-model:0.9.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}
