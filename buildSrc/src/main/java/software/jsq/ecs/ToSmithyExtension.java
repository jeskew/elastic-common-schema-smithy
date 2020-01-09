package software.jsq.ecs;

import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.jsq.ecs.model.FieldSchema;
import software.jsq.ecs.model.Schema;

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
