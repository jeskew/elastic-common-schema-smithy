package io.jsq.ecs.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(using = ReuseExpectation.Deserializer.class)
public final class ReuseExpectation {
    private final String at;
    private final String as;

    public ReuseExpectation(String at, String as) {
        this.at = Objects.requireNonNull(at);
        this.as = as;
    }

    public String getAt() {
        return this.at;
    }

    public Optional<String> getAs() {
        return Optional.ofNullable(this.as);
    }

    public static final class Deserializer extends StdDeserializer<ReuseExpectation> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ReuseExpectation deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            if (node.isTextual()) {
                String expectation = node.asText();
                return new ReuseExpectation(expectation, null);
            }

            if (node.isObject()) {
                ObjectNode obj = (ObjectNode) node;
                return new ReuseExpectation(getStringProperty(obj, "at"), getStringProperty(obj, "as"));
            }

            throw new IllegalArgumentException("ReuseExpectation must be a string or object");
        }

        private String getStringProperty(ObjectNode container, String propertyName) {
            JsonNode node = container.get(propertyName);
            if (node == null || !node.isTextual()) {
                throw new IllegalArgumentException(
                    "Supplied container has no string property at key " + propertyName);
            }

            return node.asText();
        }
    }
}
