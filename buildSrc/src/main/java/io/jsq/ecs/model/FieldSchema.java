package io.jsq.ecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = FieldSchema.Builder.class)
public final class FieldSchema {
    private final String name;
    private final Type type;
    private final Level level;
    private final Boolean required;
    private final String shortDescription;
    private final String description;
    private final Object example;
    private final Boolean index;
    private final List<AllowedValue> allowedValues;
    private final Type objectType;
    private final List<AlternateFieldDeclaration> multiFields;
    private final String format;
    private final Boolean docValues;
    private final String inputFormat;
    private final String outputFormat;
    private final Double outputPrecision;
    private final Integer ignoreAbove;

    private FieldSchema(Builder builder) {
        name = Objects.requireNonNull(builder.name);
        type = Objects.requireNonNull(builder.type);
        level = Objects.requireNonNull(builder.level);
        description = Objects.requireNonNull(builder.description);

        allowedValues = Optional.ofNullable(builder.allowedValues)
                .map(ArrayList::new)
                .map(Collections::unmodifiableList)
                .orElse(null);
        multiFields = Optional.ofNullable(builder.multiFields)
                .map(ArrayList::new)
                .map(Collections::unmodifiableList)
                .orElse(null);

        required = builder.required;
        shortDescription = builder.shortDescription;
        example = builder.example;
        index = builder.index;
        objectType = builder.objectType;
        format = builder.format;
        docValues = builder.docValues;
        inputFormat = builder.inputFormat;
        outputFormat = builder.outputFormat;
        outputPrecision = builder.outputPrecision;
        ignoreAbove = builder.ignoreAbove;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Level getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Boolean> getRequired() {
        return Optional.ofNullable(required);
    }

    public Optional<String> getShortDescription() {
        return Optional.ofNullable(shortDescription);
    }

    public Optional<Object> getExample() {
        return Optional.ofNullable(example);
    }

    public Optional<Boolean> getIndex() {
        return Optional.ofNullable(index);
    }

    public Optional<List<AllowedValue>> getAllowedValues() {
        return Optional.ofNullable(allowedValues);
    }

    public Optional<Type> getObjectType() {
        return Optional.ofNullable(objectType);
    }

    public Optional<List<AlternateFieldDeclaration>> getMultiFields() {
        return Optional.ofNullable(multiFields);
    }

    public Optional<String> getFormat() {
        return Optional.ofNullable(format);
    }

    public Optional<Boolean> getDocValues() {
        return Optional.ofNullable(docValues);
    }

    public Optional<String> getInputFormat() {
        return Optional.ofNullable(inputFormat);
    }

    public Optional<String> getOutputFormat() {
        return Optional.ofNullable(outputFormat);
    }

    public Optional<Double> getOutputPrecision() {
        return Optional.ofNullable(outputPrecision);
    }

    public Optional<Integer> getIgnoreAbove() {
        return Optional.ofNullable(ignoreAbove);
    }

    public Builder toBuilder() {
        Builder builder = builder()
                .name(getName())
                .type(getType())
                .level(getLevel())
                .description(getDescription());

        getRequired().ifPresent(builder::required);
        getShortDescription().ifPresent(builder::shortDescription);
        getExample().ifPresent(builder::example);
        getIndex().ifPresent(builder::index);
        getAllowedValues().ifPresent(builder::allowedValues);
        getObjectType().ifPresent(builder::objectType);
        getMultiFields().ifPresent(builder::multiFields);
        getFormat().ifPresent(builder::format);
        getDocValues().ifPresent(builder::docValues);
        getInputFormat().ifPresent(builder::inputFormat);
        getOutputFormat().ifPresent(builder::outputFormat);
        getOutputPrecision().ifPresent(builder::outputPrecision);
        getIgnoreAbove().ifPresent(builder::ignoreAbove);

        return builder;
    }

    public enum Type {
        @JsonProperty("long") LONG,
        @JsonProperty("keyword") KEYWORD,
        @JsonProperty("date") DATE,
        @JsonProperty("object") OBJECT,
        @JsonProperty("text") TEXT,
        @JsonProperty("ip") IP,
        @JsonProperty("geo_point") GEO_POINT,
        @JsonProperty("integer") INTEGER,
        @JsonProperty("boolean") BOOLEAN,
        @JsonProperty("float") FLOAT
    }

    public enum Level {
        @JsonProperty("core") CORE,
        @JsonProperty("extended") EXTENDED
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String name;
        private Type type;
        private Level level;
        private Boolean required;
        @JsonProperty("short") private String shortDescription;
        private String description;
        private Object example;
        private Boolean index;
        @JsonProperty("allowed_values") private List<AllowedValue> allowedValues;
        @JsonProperty("object_type") private Type objectType;
        @JsonProperty("multi_fields") private List<AlternateFieldDeclaration> multiFields;
        private String format;
        @JsonProperty("doc_values") private Boolean docValues;
        @JsonProperty("input_format") private String inputFormat;
        @JsonProperty("output_format") private String outputFormat;
        @JsonProperty("output_precision") private Double outputPrecision;
        @JsonProperty("ignore_above") private Integer ignoreAbove;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder level(Level level) {
            this.level = level;
            return this;
        }

        public Builder required(Boolean required) {
            this.required = required;
            return this;
        }

        public Builder shortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder example(Object example) {
            this.example = example;
            return this;
        }

        public Builder index(Boolean index) {
            this.index = index;
            return this;
        }

        public Builder allowedValues(List<AllowedValue> allowedValues) {
            this.allowedValues = allowedValues;
            return this;
        }

        public Builder objectType(Type objectType) {
            this.objectType = objectType;
            return this;
        }

        public Builder multiFields(List<AlternateFieldDeclaration> multiFields) {
            this.multiFields = multiFields;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder docValues(Boolean docValues) {
            this.docValues = docValues;
            return this;
        }

        public Builder inputFormat(String inputFormat) {
            this.inputFormat = inputFormat;
            return this;
        }

        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public Builder outputPrecision(Double outputPrecision) {
            this.outputPrecision = outputPrecision;
            return this;
        }

        public Builder ignoreAbove(Integer ignoreAbove) {
            this.ignoreAbove = ignoreAbove;
            return this;
        }

        public FieldSchema build() {
            return new FieldSchema(this);
        }
    }
}
