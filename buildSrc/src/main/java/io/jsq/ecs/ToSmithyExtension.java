package io.jsq.ecs;

import io.jsq.ecs.model.FieldSchema;
import io.jsq.ecs.model.Schema;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.StructureShape;

/**
 * Represents a plugin for the Elastic Common Schema -> Smithy converter that affects the converted Smithy shape.
 */
public interface ToSmithyExtension {
    default StructureShape updateStructureForSchema(StructureShape structureShape, Schema schema) {
        return structureShape;
    }

    default MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        return memberShape;
    }
}
