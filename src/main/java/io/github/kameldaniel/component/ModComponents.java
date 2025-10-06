package io.github.kameldaniel.component;

import com.mojang.serialization.Codec;
import io.github.kameldaniel.PocketContraptions;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentType<Integer> DIM_KEY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(PocketContraptions.MOD_ID, "dim_id"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static void initialize() {}
}