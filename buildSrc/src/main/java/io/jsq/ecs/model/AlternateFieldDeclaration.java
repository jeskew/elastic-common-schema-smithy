package io.jsq.ecs.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = AlternateFieldDeclaration.Builder.class)
public final class AlternateFieldDeclaration {
    private final FieldSchema.Type type;
    private final String name;

    private AlternateFieldDeclaration(Builder builder) {
        type = Objects.requireNonNull(builder.type);
        name = builder.name;
    }

    public FieldSchema.Type getType() {
        return type;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private FieldSchema.Type type;
        private String name;

        public Builder type(FieldSchema.Type type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public AlternateFieldDeclaration build() {
            return new AlternateFieldDeclaration(this);
        }
    }
}
