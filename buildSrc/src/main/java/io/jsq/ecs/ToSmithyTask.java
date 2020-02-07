package io.jsq.ecs;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ModelSerializer;

public class ToSmithyTask extends DefaultTask {
    private String targetPath;
    private String namespace;
    private String rootShapeName;

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getRootShapeName() {
        return rootShapeName;
    }

    public void setRootShapeName(String rootShapeName) {
        this.rootShapeName = rootShapeName;
    }

    @TaskAction
    public void generateSmithyModelForEcs() {
        SmithyModelBuilder builder = new SmithyModelBuilder(namespace, rootShapeName);
        Loader.loadSchemata().forEach(builder::addSchema);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(targetPath), StandardCharsets.UTF_8))) {
            writer.write(Node.prettyPrintJson(
                    ModelSerializer.builder().build().serialize(builder.build().unwrap()))
                    .trim());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
