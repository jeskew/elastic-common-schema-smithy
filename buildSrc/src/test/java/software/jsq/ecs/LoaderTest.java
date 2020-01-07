package software.jsq.ecs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import software.jsq.ecs.model.Schema;

class LoaderTest {
    @Test
    void testLoaderFindsSchemata() {
        List<Schema> schemata = Loader.loadSchemata();
        assertTrue(schemata.size() > 0);
    }
}
