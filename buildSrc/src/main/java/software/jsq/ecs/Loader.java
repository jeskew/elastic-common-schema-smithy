package software.jsq.ecs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.smithy.utils.IoUtils;
import software.jsq.ecs.model.Schema;

final class Loader {
    private static final String PATH_PREFIX = "META-INF/elastic-common-schema";
    private static final String MANIFEST_PATH = "manifest";

    static List<Schema> loadSchemata() {
        EcsFileParser parser = new EcsFileParser(new ObjectMapper(new YAMLFactory()));

        return Arrays.stream(loadSchemaManifest().trim().split(System.lineSeparator()))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Loader::ecsResource)
                .map(parser::parseEcsFile)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static String loadSchemaManifest() {
        return ecsResource(MANIFEST_PATH);
    }

    private static String ecsResource(String relativePath) {
        return IoUtils.toUtf8String(Loader.class.getResourceAsStream("/" + PATH_PREFIX + "/" + relativePath));
    }

    private static final class EcsFileParser {
        private final ObjectMapper mapper;

        EcsFileParser(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        List<Schema> parseEcsFile(String ecsResourceFile) {
            try {
                return mapper.readValue(ecsResourceFile,
                        mapper.getTypeFactory().constructCollectionType(List.class, Schema.class));
            } catch (IOException e) {
                throw new RuntimeException("Unable to parse ECS schema", e);
            }
        }
    }
}
