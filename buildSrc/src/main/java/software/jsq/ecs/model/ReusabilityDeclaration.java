package software.jsq.ecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = ReusabilityDeclaration.Builder.class)
public final class ReusabilityDeclaration {
    private final Boolean topLevel;
    private final List<String> expected;

    private ReusabilityDeclaration(Builder builder) {
        topLevel = Objects.requireNonNull(builder.topLevel);
        expected = Objects.requireNonNull(builder.expected);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Boolean getTopLevel() {
        return topLevel;
    }

    public List<String> getExpected() {
        return expected;
    }

    public Builder toBuilder() {
        return builder()
                .expected(getExpected())
                .topLevel(getTopLevel());
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        @JsonProperty("top_level") private Boolean topLevel;
        private List<String> expected;

        public Builder topLevel(Boolean topLevel) {
            this.topLevel = topLevel;
            return this;
        }

        public Builder expected(List<String> expected) {
            this.expected = expected;
            return this;
        }

        public ReusabilityDeclaration build() {
            return new ReusabilityDeclaration(this);
        }
    }
}
