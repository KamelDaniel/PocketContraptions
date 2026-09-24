package io.github.kameldaniel.component;

import com.mojang.serialization.Codec;
import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModComponents {
    // Register Components
    public static final ComponentType<Integer> DIM_KEY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            PocketContraptions.id("dim_key"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    public static final ComponentType<BlueprintBuilding> BLUEPRINT_RECIPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            PocketContraptions.id("test"),
            ComponentType.<BlueprintBuilding>builder().codec(BlueprintBuilding.Serializer.CODEC.codec()).build()
    );
    public static final ComponentType<BlueprintBuilding.Checklist> BLUEPRINT_CHECKLIST = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            PocketContraptions.id("blueprint_checklist"),
            ComponentType.<BlueprintBuilding.Checklist>builder().codec(BlueprintBuilding.Checklist.CODEC).build()
    );
    public static final ComponentType<Map<Identifier, Integer>> REQUIRED_ITEMS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            PocketContraptions.id("required_items"),
            ComponentType.<Map<Identifier, Integer>>builder().codec(Codec.unboundedMap(Identifier.CODEC, Codec.INT)).build()
    );
    public static final ComponentType<Map<Identifier, Integer>> AVAILABLE_ITEMS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            PocketContraptions.id("available_items"),
            ComponentType.<Map<Identifier, Integer>>builder().codec(Codec.unboundedMap(Identifier.CODEC, Codec.INT)).build()
    );
    public static final ComponentType<Identifier> BLUEPRINT_ID = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            PocketContraptions.id("blueprint_id"),
            ComponentType.<Identifier>builder().codec(Identifier.CODEC).build()
    );

    public static void initialize() {}
}