package io.jsq.ecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = ReusabilityDeclaration.Builder.class)
public final class ReusabilityDeclaration {
    private final Boolean topLevel;
    private final List<ReuseExpectation> expected;
    private final Integer order;

    private ReusabilityDeclaration(Builder builder) {
        topLevel = Objects.requireNonNull(builder.topLevel);
        expected = List.copyOf(Objects.requireNonNull(builder.expected));
        order = builder.order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Boolean getTopLevel() {
        return topLevel;
    }

    public List<ReuseExpectation> getExpected() {
        return expected;
    }

    public Optional<Integer> getOrder() {
        return Optional.ofNullable(order);
    }

    public Builder toBuilder() {
        Builder builder = builder()
            .expected(getExpected())
            .topLevel(getTopLevel());

        getOrder().ifPresent(builder::order);

        return builder;

    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        @JsonProperty("top_level") private Boolean topLevel;
        private List<ReuseExpectation> expected;
        private Integer order;

        public Builder topLevel(Boolean topLevel) {
            this.topLevel = topLevel;
            return this;
        }

        public Builder expected(List<ReuseExpectation> expected) {
            this.expected = expected;
            return this;
        }

        public Builder order(Integer order) {
            this.order = order;
            return this;
        }

        public ReusabilityDeclaration build() {
            return new ReusabilityDeclaration(this);
        }
    }
}
