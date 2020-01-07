package software.jsq.ecs;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.validation.ValidatedResult;
import software.jsq.ecs.model.Schema;

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
}
