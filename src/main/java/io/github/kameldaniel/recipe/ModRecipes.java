package io.github.kameldaniel.recipe;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeType<BlueprintBuilding> BLUEPRINT_BUILDING = register("blueprint_building");
    public static final BlueprintBuilding.Serializer BLUEPRINT_BUILDING_SERIALIZER = register("blueprint_building", new BlueprintBuilding.Serializer());

    public static <T extends Recipe<?>> RecipeType<T> register(String id) {
        return Registry.register(Registries.RECIPE_TYPE, PocketContraptions.id(id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, PocketContraptions.id(id), serializer);
    }

    public static void initialize() {
        PocketContraptions.LOGGER.info("ModRecipes initialized");
    }
}
