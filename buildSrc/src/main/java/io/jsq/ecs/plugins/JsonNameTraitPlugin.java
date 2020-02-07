package io.jsq.ecs.plugins;

import io.jsq.ecs.ToSmithyExtension;
import io.jsq.ecs.model.FieldSchema;
import java.util.Optional;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.JsonNameTrait;

public final class JsonNameTraitPlugin implements ToSmithyExtension  {
    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        return Optional.of(fieldSchema.getName())
                .filter(name -> !name.equals(memberShape.getMemberName()))
                .map(jsonName -> memberShape.toBuilder().addTrait(new JsonNameTrait(jsonName)).build())
                .orElse(memberShape);
    }
}
