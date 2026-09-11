package io.github.kameldaniel.recipe.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.item.ModItems;
import io.github.kameldaniel.recipe.ModRecipes;
import io.github.kameldaniel.recipe.input.BlueprintBuildingInput;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BlueprintBuilding implements Recipe<BlueprintBuildingInput> {
    private static final List<Identifier> ids = new ArrayList<>();

    final Identifier id;
    final Ingredient blueprint;
    final Ingredient base;
    final List<CountedIngredient> ingredients;
    final ItemStack result;

    public BlueprintBuilding(Identifier id, Ingredient blueprint, Ingredient base, ItemStack result, List<CountedIngredient> ingredients) {
        this.id = id;
        this.blueprint = blueprint;
        this.base = base;
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
        if (id == null) return;
        if (ids.contains(id)) PocketContraptions.LOGGER.warn("Multiple Blueprint recipes have id '{}'", id);
        else ids.add(id);
    }

    @Override
    public boolean matches(BlueprintBuildingInput input, World world) {
        return input.getBlueprint().contains(ModComponents.BLUEPRINT_ID)
                ? Objects.equals(input.getBlueprint().get(ModComponents.BLUEPRINT_ID), id)
                : input.getBase().isEmpty() ? blueprint.test(input.getBlueprint())
                : blueprint.test(input.getBlueprint()) && base.test(input.getBase());
    }

    @Override
    public ItemStack craft(BlueprintBuildingInput input, RegistryWrapper.WrapperLookup registries) {
        return result.copy();
    }

    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public RecipeSerializer<? extends Recipe<BlueprintBuildingInput>> getSerializer() {
        return ModRecipes.BLUEPRINT_BUILDING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<BlueprintBuildingInput>> getType() {
        return ModRecipes.BLUEPRINT_BUILDING;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forSingleSlot(this.base);
    }

    // the transaction should be aborted or committed by the caller
    public Checklist getChecklist(Transaction transaction, BlueprintBuildingInput input) {
        ArrayList<CountedIngredient> required = new ArrayList<>();
        ArrayList<CountedIngredient> available = new ArrayList<>();
        for (CountedIngredient ingredient : List.copyOf(this.ingredients)) {
            CountedIngredient requiredCI = new CountedIngredient(ingredient.getIngredient(), ingredient.getCount());
            CountedIngredient availableCI = new CountedIngredient(ingredient.getIngredient());
            for (Storage<ItemVariant> inventory : input.getAvailableInventories()) {
                for (StorageView<ItemVariant> view : inventory) {
                    if (requiredCI.getIngredient().test(view.getResource().toStack())) {
                        int count = (int) inventory.extract(view.getResource(), requiredCI.getCount(), transaction);
                        requiredCI.count -= count;
                        availableCI.count += count;
                        if (requiredCI.count == 0) break;
                    }
                }
                if (requiredCI.count == 0) break;
            }
            if (requiredCI.count > 0) required.add(requiredCI);
            if (availableCI.count > 0) available.add(availableCI);
        }
        return new Checklist(required, available);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return new RecipeBookCategory();
    }

    public record Checklist(List<CountedIngredient> required, List<CountedIngredient> available) {
        public static final Codec<Checklist> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        CountedIngredient.CODEC.listOf().optionalFieldOf("required", List.of()).forGetter(Checklist::required),
                        CountedIngredient.CODEC.listOf().optionalFieldOf("available", List.of()).forGetter(Checklist::available)
                ).apply(instance, Checklist::new)
        );

        public static Checklist fromRequired(List<CountedIngredient> required) {
            return new Checklist(List.copyOf(required), required.stream().map(ci -> new CountedIngredient(ci.ingredient)).toList());
        }

        public boolean isComplete() {
            return this.required.isEmpty();
        }
    }

    public static class CountedIngredient {
        private final Ingredient ingredient;
        private int count;

        public CountedIngredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            this.count = 0;
        }

        public CountedIngredient(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = count;
        }

        public Ingredient getIngredient() {
            return count <= 0 ? Ingredient.ofItem(Items.AIR) : ingredient;
        }

        public int getCount() {
            return count;
        }

        public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(ci -> ci.ingredient),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(ci -> ci.count)
                ).apply(instance, CountedIngredient::new)
        );
        public static final PacketCodec<RegistryByteBuf, CountedIngredient> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC,
                recipe -> recipe.ingredient,
                PacketCodecs.INTEGER,
                recipe -> recipe.count,
                CountedIngredient::new
        );
    }

    public static class Serializer implements RecipeSerializer<BlueprintBuilding> {
        public static final MapCodec<BlueprintBuilding> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Identifier.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.id),
                        Ingredient.CODEC.optionalFieldOf("blueprint", Ingredient.ofItem(ModItems.BLUEPRINT)).forGetter(recipe -> recipe.blueprint),
                        Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                        CountedIngredient.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(recipe -> recipe.ingredients)
                ).apply(instance, BlueprintBuilding::new)
        );

        public static final PacketCodec<RegistryByteBuf, BlueprintBuilding> PACKET_CODEC = PacketCodec.tuple(
                Identifier.PACKET_CODEC,
                recipe -> recipe.id,
                Ingredient.PACKET_CODEC,
                recipe -> recipe.blueprint,
                Ingredient.PACKET_CODEC,
                recipe -> recipe.base,
                ItemStack.PACKET_CODEC,
                recipe -> recipe.result,
                CountedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()),
                recipe -> recipe.ingredients,
                BlueprintBuilding::new
        );

        @Override
        public MapCodec<BlueprintBuilding> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BlueprintBuilding> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
