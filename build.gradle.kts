import java.io.PrintWriter

group = "software.jsq.ecs"
version = "0.0.1-SNAPSHOT"

plugins {
    id("software.jsq.ecs.tosmithy")
    java
    maven
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val modelTargetDir = file("${sourceSets["main"].output.resourcesDir}/META-INF/smithy")
val writeModel = tasks.getByName<software.jsq.ecs.ToSmithyTask>("writeModel") {
    doFirst {
        modelTargetDir.mkdirs()
    }

    namespace = "elastic.ecs"
    rootShapeName = "Record"
    targetPath = modelTargetDir.absolutePath + "/elastic-common-schema.json"

    doLast {
        val smithyManifest = file(modelTargetDir.absolutePath + "/manifest")
        val writer = PrintWriter(smithyManifest.absolutePath)
        writer.append("/META-INF/smithy/elastic-common-schema.json\n")
        writer.close()
    }
}

tasks.jar {
    dependsOn(writeModel)
}
