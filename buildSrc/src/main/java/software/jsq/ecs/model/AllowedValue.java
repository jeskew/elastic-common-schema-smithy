package software.jsq.ecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = AllowedValue.Builder.class)
public final class AllowedValue {
    private final String name;
    private final String description;
    private final List<String> expectedEventTypes;

    private AllowedValue(Builder builder) {
        name = Objects.requireNonNull(builder.name);
        description = Objects.requireNonNull(builder.description);
        expectedEventTypes = Optional.ofNullable(builder.expectedEventTypes)
                .map(ArrayList::new)
                .map(Collections::unmodifiableList)
                .orElse(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<List<String>> getExpectedEventTypes() {
        return Optional.ofNullable(expectedEventTypes);
    }

    public Builder toBuilder() {
        Builder builder = builder()
                .name(getName())
                .description(getDescription());

        getExpectedEventTypes().ifPresent(builder::expectedEventTypes);

        return builder;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String name;
        private String description;
        @JsonProperty("expected_event_types") private List<String> expectedEventTypes;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder expectedEventTypes(List<String> expectedEventTypes) {
            this.expectedEventTypes = expectedEventTypes;
            return this;
        }

        public AllowedValue build() {
            return new AllowedValue(this);
        }
    }
}
