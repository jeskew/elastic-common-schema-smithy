package io.jsq.ecs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsq.ecs.model.Schema;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoaderTest {
    @Test
    void testLoaderFindsSchemata() {
        List<Schema> schemata = Loader.loadSchemata();
        assertTrue(schemata.size() > 0);
    }
}
