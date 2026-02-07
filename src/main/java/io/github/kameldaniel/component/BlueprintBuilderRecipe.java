package io.github.kameldaniel.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Map;

public record BlueprintBuilderRecipe(Identifier base, Map<Identifier, Integer> ingredients, Identifier result) {
    public static final Codec<BlueprintBuilderRecipe> CODEC = RecordCodecBuilder.create(
            record -> record.group(
                    Identifier.CODEC.fieldOf("base").forGetter(BlueprintBuilderRecipe::base),
                    Codec.unboundedMap(Identifier.CODEC, Codec.INT).fieldOf("ingredients").forGetter(BlueprintBuilderRecipe::ingredients),
                    Identifier.CODEC.fieldOf("result").forGetter(BlueprintBuilderRecipe::result)
            ).apply(record, BlueprintBuilderRecipe::new)
    );
}
