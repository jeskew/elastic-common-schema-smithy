package software.jsq.ecs.plugins;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.EnumConstantBody;
import software.amazon.smithy.model.traits.EnumTrait;
import software.jsq.ecs.ToSmithyExtension;
import software.jsq.ecs.model.AllowedValue;
import software.jsq.ecs.model.FieldSchema;

public final class EnumTraitPlugin implements ToSmithyExtension  {
    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        return fieldSchema.getAllowedValues()
                .map(EnumTraitPlugin::fromAllowedValues)
                .map(trait -> memberShape.toBuilder().addTrait(trait).build())
                .orElse(memberShape);
    }

    private static EnumTrait fromAllowedValues(List<AllowedValue> allowedValues) {
        EnumTrait.Builder enumBuilder = EnumTrait.builder();
        allowedValues.forEach(value -> enumBuilder.addEnum(value.getName(), EnumConstantBody.builder()
                .name(Arrays.stream(value.getName().split("\\W+"))
                        .filter(str -> !str.isEmpty())
                        .map(String::toUpperCase)
                        .collect(Collectors.joining("_")))
                .documentation(value.getDescription().trim())
                .build()));
        return enumBuilder.build();
    }
}
