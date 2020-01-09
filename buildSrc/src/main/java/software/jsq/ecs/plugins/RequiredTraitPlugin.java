package software.jsq.ecs.plugins;

import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.RequiredTrait;
import software.jsq.ecs.ToSmithyExtension;
import software.jsq.ecs.model.FieldSchema;

public final class RequiredTraitPlugin implements ToSmithyExtension  {
    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        return fieldSchema.getRequired()
                .filter(Boolean::booleanValue)
                .map(req -> memberShape.toBuilder().addTrait(new RequiredTrait()).build())
                .orElse(memberShape);
    }
}
