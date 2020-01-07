package software.jsq.ecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.loader.ModelAssembler;
import software.amazon.smithy.model.loader.Prelude;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.model.traits.EnumConstantBody;
import software.amazon.smithy.model.traits.EnumTrait;
import software.amazon.smithy.model.traits.RequiredTrait;
import software.amazon.smithy.model.validation.ValidatedResult;
import software.amazon.smithy.utils.Pair;
import software.jsq.ecs.model.FieldSchema;
import software.jsq.ecs.model.ReusabilityDeclaration;
import software.jsq.ecs.model.Schema;

final class SmithyModelBuilder {
    private final String namespace;
    private final String rootShapeName;
    private final ShapeId rootId;
    private final Map<ShapeId, Shape> indexBuilder = new HashMap<>();
    private final Map<Pair<String, ShapeId>, List<String>> reuseDirectives = new HashMap<>();

    SmithyModelBuilder(String namespace, String rootShapeName) {
        this.namespace = Objects.requireNonNull(namespace);
        this.rootShapeName = Objects.requireNonNull(rootShapeName);
        rootId = ShapeId.fromParts(namespace, rootShapeName);
        indexBuilder.put(rootId, StructureShape.builder().id(rootId).build());
    }

    void addSchema(Schema schema) {
        ShapeId shape = fromSchema(schema);

        if (!schema.getRoot().orElse(false)
                && schema.getReusable().map(ReusabilityDeclaration::getTopLevel).orElse(true)) {
            indexBuilder.put(rootId, getRootShape().toBuilder()
                    .addMember(MemberShape.builder()
                            .target(shape)
                            .id(rootId.withMember(schema.getName()))
                            .build())
                    .build());
        }

        schema.getReusable()
                .map(ReusabilityDeclaration::getExpected)
                .ifPresent(reuses -> reuseDirectives.put(new Pair<>(schema.getName(), shape), reuses));
    }

    ValidatedResult<Model> build() {
        for (Map.Entry<Pair<String, ShapeId>, List<String>> entry : reuseDirectives.entrySet()) {
            for (String keyOfReusingMember : entry.getValue()) {
                StructureShape reUser = getRootShape().getMember(keyOfReusingMember)
                        .map(MemberShape::getTarget)
                        .map(indexBuilder::get)
                        .flatMap(Shape::asStructureShape)
                        .orElseThrow(() -> new RuntimeException(
                                "Unable to reuse " + entry.getKey().getLeft() + " under key " + keyOfReusingMember));
                indexBuilder.put(reUser.getId(), reUser.toBuilder()
                        .addMember(MemberShape.builder()
                                .target(entry.getKey().getRight())
                                .id(reUser.getId().withMember(entry.getKey().getLeft()))
                                .build())
                        .build());
            }
        }

        ModelAssembler assembler = Model.assembler(getClass().getClassLoader());
        indexBuilder.values().forEach(assembler::addShape);

        return assembler.assemble();
    }

    private StructureShape getRootShape() {
        return Optional.ofNullable(indexBuilder.get(rootId))
                .flatMap(Shape::asStructureShape)
                .orElseThrow(() -> new RuntimeException("Root shape not found. Was it deleted from the index?"));
    }

    private ShapeId fromSchema(Schema schema) {
        final String shapeName = titleCase(schema.getRoot()
                .filter(Boolean::booleanValue)
                .map(t -> rootShapeName)
                .orElseGet(schema::getTitle));

        ShapeId schemaRoot = ShapeId.fromParts(namespace, shapeName);
        indexBuilder.computeIfAbsent(schemaRoot, sid -> fromSchema(sid, schema));

        // ECS will refer to nested shapes within by using period delimited names, e.g., "response.body.bytes" under the
        // "http" schema. In the Smithy model, each intermediate shape needs to be represented as a distinct structure.
        Map<List<String>, List<FieldSchema>> nested = schema.getFields().orElse(Collections.emptyList()).stream()
                .map(fs -> {
                    List<String> keySequence = Arrays.asList(fs.getName().split("\\."));
                    return new Pair<>(keySequence.subList(0, keySequence.size() - 1),
                            fs.toBuilder().name(keySequence.get(keySequence.size() - 1)).build());
                })
                .collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList())));

        // Add to the index all shapes necessary to add this schema to the model
        nested.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().size()))
                // Ensure intermediate shapes exist and are properly linked
                .peek(entry -> registerAndLinkIntermediateMembers(shapeName, entry.getKey()))
                .map(entry -> fromFields(fetchStructureShape(composeShapeId(shapeName, entry.getKey())),
                        entry.getValue()))
                .forEach(composed -> indexBuilder.put(composed.getId(), composed));

        return schemaRoot;
    }

    private ShapeId composeShapeId(String topLevelShapeName, List<String> intermediateNames) {
        return ShapeId.fromParts(namespace,
                Stream.concat(Stream.of(topLevelShapeName),
                        intermediateNames.stream().map(SmithyModelBuilder::titleCase)
                                .map(str -> str.replaceAll("[^a-zA-Z0-9]", "_")))
                        .collect(Collectors.joining()));
    }

    private Pair<ShapeId, Set<Shape>> fromFieldSchema(ShapeId id, FieldSchema fieldSchema) {
        switch (fieldSchema.getType()) {
            case IP:
            case TEXT:
            case KEYWORD:
                return forScalar("String");
            case DATE:
                return forScalar("Timestamp");
            case BOOLEAN:
                return forScalar("Boolean");
            case LONG:
                return forScalar("Long");
            case FLOAT:
                return forScalar("Float");
            case INTEGER:
                return forScalar("Integer");
            case OBJECT:
                if (fieldSchema.getObjectType().isPresent()) {
                    Pair<ShapeId, Set<Shape>> memberTarget = fromFieldSchema(
                            ShapeId.fromParts(id.getNamespace(), id.getName() + "Member"),
                            fieldSchema.toBuilder()
                                    .type(fieldSchema.getObjectType().orElseThrow(
                                            () -> new RuntimeException("No object_type supplied for " + id)))
                                    .build());
                    MemberShape key = MemberShape.builder()
                            .id(id.withMember("key"))
                            .target(ShapeId.fromParts(Prelude.NAMESPACE, "String"))
                            .build();
                    MemberShape value = MemberShape.builder()
                            .id(id.withMember("value"))
                            .target(memberTarget.getLeft())
                            .build();
                    MapShape targetShape = MapShape.builder()
                            .id(id)
                            .key(key)
                            .value(value)
                            .build();
                    return new Pair<>(id,
                            Stream.concat(memberTarget.getRight().stream(), Stream.of(key, value, targetShape))
                                    .collect(Collectors.toSet()));
                } else {
                    return new Pair<>(id, Collections.singleton(StructureShape.builder().id(id).build()));
                }
            case GEO_POINT:
                MemberShape latitude = MemberShape.builder()
                        .id(id.withMember("lat"))
                        .target(ShapeId.fromParts(Prelude.NAMESPACE, "Double"))
                        .build();
                MemberShape longitude = MemberShape.builder()
                        .id(id.withMember("lon"))
                        .target(ShapeId.fromParts(Prelude.NAMESPACE, "Double"))
                        .build();
                return new Pair<>(id, Stream.of(latitude, longitude,
                        StructureShape.builder()
                                .id(id)
                                .addMember(latitude)
                                .addMember(longitude)
                                .build())
                        .collect(Collectors.toSet()));
            default:
                throw new RuntimeException("Unrecognized field type: " + fieldSchema.getType());
        }
    }

    private StructureShape fetchStructureShape(ShapeId id) {
        return Optional.ofNullable(indexBuilder.get(id))
                .flatMap(Shape::asStructureShape)
                .orElseThrow(() -> new RuntimeException("Could not find a structure shape named " + id));
    }

    private Pair<ShapeId, Set<Shape>> forScalar(String shapeName) {
        return new Pair<>(ShapeId.fromParts(Prelude.NAMESPACE, shapeName), Collections.emptySet());
    }

    private void registerAndLinkIntermediateMembers(String shapeName, List<String> intermediateKeys) {
        for (int i = 0; i < intermediateKeys.size(); i++) {
            ShapeId shapeId = composeShapeId(shapeName, intermediateKeys.subList(0, i + 1));
            indexBuilder.computeIfAbsent(shapeId, sid -> StructureShape.builder().id(sid).build());

            StructureShape parent = fetchStructureShape(composeShapeId(shapeName, intermediateKeys.subList(0, i)));
            if (!parent.getMember(intermediateKeys.get(i)).isPresent()) {
                indexBuilder.put(parent.getId(), parent.toBuilder()
                        .addMember(MemberShape.builder()
                                .target(shapeId)
                                .id(parent.getId().withMember(intermediateKeys.get(i)))
                                .build())
                        .build());
            }
        }
    }

    private StructureShape fromFields(StructureShape target, List<FieldSchema> members) {
        StructureShape.Builder builder = target.toBuilder();
        for (FieldSchema field : members) {
            String sanitizedName = field.getName().replaceAll("[^a-zA-Z0-9]", "_");
            Pair<ShapeId, Set<Shape>> converted = fromFieldSchema(ShapeId.fromParts(target.getId().getNamespace(),
                    target.getId().getName() + titleCase(sanitizedName)), field);
            converted.getRight().forEach(s -> indexBuilder.put(s.getId(), s));

            MemberShape.Builder memberBuilder = MemberShape.builder()
                    .target(converted.getLeft())
                    .id(target.getId().withMember(sanitizedName))
                    .addTrait(new DocumentationTrait(field.getDescription(), SourceLocation.NONE));

            field.getAllowedValues()
                    .map(values -> {
                        EnumTrait.Builder enumBuilder = EnumTrait.builder();
                        values.forEach(value -> enumBuilder.addEnum(value.getName(), EnumConstantBody.builder()
                                .documentation(value.getDescription())
                                .build()));
                        return enumBuilder.build();
                    })
                    .ifPresent(memberBuilder::addTrait);

            field.getRequired()
                    .filter(Boolean::booleanValue)
                    .map(b -> new RequiredTrait())
                    .ifPresent(memberBuilder::addTrait);

            builder.addMember(memberBuilder.build()).build();
        }

        return builder.build();
    }

    private StructureShape fromSchema(ShapeId id, Schema schema) {
        return StructureShape.builder()
                .id(id)
                .addTrait(new DocumentationTrait(schema.getDescription(), SourceLocation.NONE))
                .build();
    }

    private static String titleCase(String toCapitalize) {
        return Stream.of(toCapitalize)
                .flatMap(str -> Arrays.stream(toCapitalize.split("\\s+")))
                .filter(str -> !str.isEmpty())
                .map(str -> str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }
}
