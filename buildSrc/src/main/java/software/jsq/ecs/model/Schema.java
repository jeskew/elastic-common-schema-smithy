package software.jsq.ecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = Schema.Builder.class)
public final class Schema {
    private final String name;
    private final String title;
    private final String description;
    private final SchemaType type;
    private final Integer group;
    private final String shortDescription;
    private final String footnote;
    private final Boolean root;
    private final ReusabilityDeclaration reusable;
    private final List<FieldSchema> fields;

    private Schema(Builder builder) {
        name = Objects.requireNonNull(builder.name);
        title = Objects.requireNonNull(builder.title);
        description = Objects.requireNonNull(builder.description);
        type = Objects.requireNonNull(builder.type);

        fields = Optional.ofNullable(builder.fields)
                .map(ArrayList::new)
                .map(Collections::unmodifiableList)
                .orElse(null);

        group = builder.group;
        shortDescription = builder.shortDescription;
        footnote = builder.footnote;
        root = builder.root;
        reusable = builder.reusable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SchemaType getType() {
        return type;
    }

    public Optional<Integer> getGroup() {
        return Optional.ofNullable(group);
    }

    public Optional<List<FieldSchema>> getFields() {
        return Optional.ofNullable(fields);
    }

    public Optional<String> getShortDescription() {
        return Optional.ofNullable(shortDescription);
    }

    public Optional<String> getFootnote() {
        return Optional.ofNullable(footnote);
    }

    public Optional<Boolean> getRoot() {
        return Optional.ofNullable(root);
    }

    public Optional<ReusabilityDeclaration> getReusable() {
        return Optional.ofNullable(reusable);
    }

    public Builder toBuilder() {
        Builder builder = builder()
                .name(getName())
                .title(getTitle())
                .type(getType());

        getGroup().ifPresent(builder::group);
        getFields().ifPresent(builder::fields);
        getShortDescription().ifPresent(builder::shortDescription);
        getRoot().ifPresent(builder::root);
        getReusable().ifPresent(builder::reusable);

        return builder;
    }

    public enum SchemaType { group }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String name;
        private String title;
        private String description;
        private SchemaType type;
        private Integer group;
        @JsonProperty("short") private String shortDescription;
        private String footnote;
        private Boolean root;
        private ReusabilityDeclaration reusable;
        private List<FieldSchema> fields;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(SchemaType type) {
            this.type = type;
            return this;
        }

        public Builder group(Integer group) {
            this.group = group;
            return this;
        }

        public Builder shortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder footnote(String footnote) {
            this.footnote = footnote;
            return this;
        }

        public Builder root(Boolean root) {
            this.root = root;
            return this;
        }

        public Builder reusable(ReusabilityDeclaration reusable) {
            this.reusable = reusable;
            return this;
        }

        public Builder fields(List<FieldSchema> fields) {
            this.fields = fields;
            return this;
        }

        public Schema build() {
            return new Schema(this);
        }
    }
}
