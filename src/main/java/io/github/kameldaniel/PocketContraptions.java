package io.github.kameldaniel;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.kameldaniel.block.ModBlockEntities;
import io.github.kameldaniel.block.ModBlocks;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.item.ModItems;
import io.github.kameldaniel.payload.ModPayloads;
import io.github.kameldaniel.recipe.ModRecipes;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import io.github.kameldaniel.screenhandler.ModScreenHandlers;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PocketContraptions implements ModInitializer {
	public static final String MOD_ID = "pocket-contraptions";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

	@Override
	public void onInitialize() {
        // Initialize Mod Classes
        ModItems.initialize();
        ModBlocks.initialize();
        PocketDimension.initialize();
        ModComponents.initialize();
        ModBlockEntities.initialize();
        ModScreenHandlers.initialize();
        ModPayloads.init();
        ModRecipes.initialize();
//        EventRegistrar.initialize();
		LOGGER.info("Mod Initialized! Hello Fabric World!");

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("makeblueprint")
                    .then(CommandManager.argument("base", ItemStackArgumentType.itemStack(commandRegistryAccess))
                    .then(CommandManager.argument("result", ItemStackArgumentType.itemStack(commandRegistryAccess))
                    .then(CommandManager.argument("ingredient", ItemStackArgumentType.itemStack(commandRegistryAccess))
                    .then(CommandManager.argument("count", IntegerArgumentType.integer())
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty()) {
                            context.getSource().sendError(Text.literal("No item in main hand!"));
                            return 0;
                        }

                        Ingredient base = Ingredient.ofItem(ItemStackArgumentType.getItemStackArgument(context, "base").getItem());
                        ItemStack result = ItemStackArgumentType.getItemStackArgument(context, "result").createStack(1, false);
                        Ingredient ingredient = Ingredient.ofItem(ItemStackArgumentType.getItemStackArgument(context, "ingredient").getItem());
                        int count = IntegerArgumentType.getInteger(context, "count");
                        BlueprintBuilding.CountedIngredient ci = new BlueprintBuilding.CountedIngredient(ingredient, count);
                        BlueprintBuilding recipe = new BlueprintBuilding(null, null,  base, result, List.of(ci));
                        stack.set(ModComponents.BLUEPRINT_RECIPE, recipe);
                        return 1;
                    }))))));

            commandDispatcher.register(CommandManager.literal("addexampleblueprint")
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty()) {
                            context.getSource().sendError(Text.literal("No item in main hand!"));
                            return 0;
                        }

                        Ingredient base = Ingredient.ofItem(Items.DIAMOND_BLOCK);

                        Map<Identifier, Integer> ingredientsMap = new HashMap<>();
                        ingredientsMap.put(Identifier.ofVanilla("diamond"), 100);
                        ingredientsMap.put(Identifier.ofVanilla("dirt"), 50);
                        ingredientsMap.put(Identifier.ofVanilla("short_grass"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("grass_block"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("gold_ingot"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("iron_ingot"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("copper_ingot"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("gold_block"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("iron_block"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("copper_block"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("gold_nugget"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("iron_nugget"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("sand"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("gravel"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("blaze_rod"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("netherrack"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("snowball"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("pumpkin"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("melon"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("tall_grass"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("golden_apple"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("wheat_seeds"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("brick"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("nether_brick"), 3);
                        ingredientsMap.put(Identifier.ofVanilla("chest"), 3);

                        List<BlueprintBuilding.CountedIngredient> ingredients = ingredientsMap.entrySet().stream().map(
                                (entry) -> new BlueprintBuilding.CountedIngredient(Ingredient.ofItem(Registries.ITEM.get(entry.getKey())), entry.getValue())
                        ).toList();

                        ItemStack result = new ItemStack(Items.NETHERITE_BLOCK, 1);

                        BlueprintBuilding recipe = new BlueprintBuilding(null, null, base, result, ingredients);
                        stack.set(ModComponents.BLUEPRINT_RECIPE, recipe);
                        return 1;
                    }));
            commandDispatcher.register(CommandManager.literal("removeblueprint")
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty()) {
                            context.getSource().sendError(Text.literal("No item in main hand!"));
                            return 0;
                        }
                        stack.remove(ModComponents.BLUEPRINT_RECIPE);
                        return 1;
                    }));
        }));
	}
}