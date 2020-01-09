package software.jsq.ecs.plugins;

import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.jsq.ecs.ToSmithyExtension;
import software.jsq.ecs.model.FieldSchema;
import software.jsq.ecs.model.Schema;

public final class DocumentationTraitPlugin implements ToSmithyExtension {
    public StructureShape updateStructureForSchema(StructureShape structureShape, Schema schema) {
        if (!structureShape.hasTrait("documentation")) {
            return structureShape.toBuilder()
                    .addTrait(new DocumentationTrait(schema.getDescription().trim()))
                    .build();
        }

        return structureShape;
    }

    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        if (!memberShape.hasTrait("documentation")) {
            return memberShape.toBuilder()
                    .addTrait(new DocumentationTrait(fieldSchema.getDescription().trim()))
                    .build();
        }

        return memberShape;
    }
}
