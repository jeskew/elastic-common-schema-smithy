package software.jsq.ecs.plugins;

import java.util.Optional;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.JsonNameTrait;
import software.jsq.ecs.ToSmithyExtension;
import software.jsq.ecs.model.FieldSchema;

public final class JsonNameTraitPlugin implements ToSmithyExtension  {
    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        return Optional.of(fieldSchema.getName())
                .filter(name -> !name.equals(memberShape.getMemberName()))
                .map(jsonName -> memberShape.toBuilder().addTrait(new JsonNameTrait(jsonName)).build())
                .orElse(memberShape);
    }
}
