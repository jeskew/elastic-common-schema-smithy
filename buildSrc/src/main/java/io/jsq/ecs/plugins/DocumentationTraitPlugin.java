package io.jsq.ecs.plugins;

import io.jsq.ecs.ToSmithyExtension;
import io.jsq.ecs.model.FieldSchema;
import io.jsq.ecs.model.Schema;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.DocumentationTrait;

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
