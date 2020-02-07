package io.jsq.ecs.plugins;

import io.jsq.ecs.ToSmithyExtension;
import io.jsq.ecs.model.FieldSchema;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.RequiredTrait;

public final class RequiredTraitPlugin implements ToSmithyExtension  {
    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        return fieldSchema.getRequired()
                .filter(Boolean::booleanValue)
                .map(req -> memberShape.toBuilder().addTrait(new RequiredTrait()).build())
                .orElse(memberShape);
    }
}
