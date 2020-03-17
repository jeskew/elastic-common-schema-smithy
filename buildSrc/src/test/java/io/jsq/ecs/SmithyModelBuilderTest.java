package io.jsq.ecs;

import io.jsq.ecs.model.Schema;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeIndex;
import software.amazon.smithy.model.validation.ValidatedResult;

class SmithyModelBuilderTest {
    @Test
    void testModelBuilderBuildsValidModel() {
        List<Schema> schemata = Loader.loadSchemata();
        SmithyModelBuilder builder = new SmithyModelBuilder("example.test", "Record");
        schemata.forEach(builder::addSchema);
        ValidatedResult<Model> result = builder.build();

        Assertions.assertFalse(result.isBroken());
        Assertions.assertTrue(result.getResult().isPresent());
    }

    @Test
    void testModelBuilderCorrectlyAppliesArrayNormalization() {
        List<Schema> schemata = Loader.loadSchemata();
        SmithyModelBuilder builder = new SmithyModelBuilder("example.test", "Record");
        schemata.forEach(builder::addSchema);
        ShapeIndex index = builder.build().unwrap().getShapeIndex();

        Shape rootShape = index.getShape(ShapeId.from("example.test#Record")).get();
        MemberShape tagsMember = rootShape.asStructureShape().flatMap(ss -> ss.getMember("tags")).get();
        Shape tagsTarget = index.getShape(tagsMember.getTarget()).get();
        Assertions.assertTrue(tagsTarget.isListShape());
    }
}
