package io.jsq.ecs.plugins;

import io.jsq.ecs.ToSmithyExtension;
import io.jsq.ecs.model.FieldSchema;
import java.util.Optional;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.TagsTrait;

public class TagsTraitPlugin implements ToSmithyExtension  {
    public MemberShape updateMemberForField(MemberShape memberShape, FieldSchema fieldSchema) {
        TagsTrait.Builder traitBuilder = memberShape.getTrait(TagsTrait.class)
                .map(TagsTrait::toBuilder)
                .orElseGet(TagsTrait::builder);
        Optional.of(fieldSchema.getLevel())
                .filter(l -> l != FieldSchema.Level.CORE)
                .ifPresent(l -> traitBuilder.addValue("ecs:" + l.toString().toLowerCase()));
        fieldSchema.getIndex()
                .filter(b -> !b)
                .ifPresent(_b -> traitBuilder.addValue("ecs:unindexed"));

        return Optional.of(traitBuilder.build())
                .filter(trait -> !trait.getValues().isEmpty())
                .map(trait -> memberShape.toBuilder().addTrait(trait).build())
                .orElse(memberShape);
    }
}
