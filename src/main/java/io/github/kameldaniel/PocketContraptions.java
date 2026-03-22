package io.github.kameldaniel;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.kameldaniel.block.ModBlockEntities;
import io.github.kameldaniel.block.ModBlocks;
import io.github.kameldaniel.component.BlueprintBuilderRecipe;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.item.ModItems;
import io.github.kameldaniel.payload.ModPayloads;
import io.github.kameldaniel.screenhandler.ModScreenHandlers;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
//        EventRegistrar.init();
		LOGGER.info("Mod Initialized! Hello Fabric World!");

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("makeblueprint")
                    .then(CommandManager.argument("base", ItemStackArgumentType.itemStack(commandRegistryAccess))
                    .then(CommandManager.argument("result", ItemStackArgumentType.itemStack(commandRegistryAccess))
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty()) {
                            context.getSource().sendError(Text.literal("No item in main hand!"));
                            return 0;
                        }

                        Identifier base = Registries.ITEM.getId(ItemStackArgumentType.getItemStackArgument(context, "base").getItem());
                        Identifier result = Registries.ITEM.getId(ItemStackArgumentType.getItemStackArgument(context, "result").getItem());

                        BlueprintBuilderRecipe recipe = new BlueprintBuilderRecipe(base, new HashMap<>(), result);
                        stack.set(ModComponents.BLUEPRINT, recipe);
                        return 1;
                    }))));

            commandDispatcher.register(CommandManager.literal("addingredient")
                    .then(CommandManager.argument("ingredient", ItemStackArgumentType.itemStack(commandRegistryAccess))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty() || !stack.contains(ModComponents.BLUEPRINT)) {
                            context.getSource().sendError(Text.literal("No blueprint in main hand!"));
                            return 0;
                        }

                        Identifier ingredient = Registries.ITEM.getId(ItemStackArgumentType.getItemStackArgument(context, "ingredient").getItem());
                        int amount = IntegerArgumentType.getInteger(context, "amount");

                        Identifier base = stack.get(ModComponents.BLUEPRINT).base();
                        Map<Identifier, Integer> ingredients = new HashMap<>(stack.get(ModComponents.BLUEPRINT).ingredients());
                        ingredients.put(ingredient, amount);
                        Identifier result = stack.get(ModComponents.BLUEPRINT).result();

                        ItemStack newBlueprint = stack.copy();
                        newBlueprint.set(ModComponents.BLUEPRINT, new BlueprintBuilderRecipe(base, ingredients, result));
                        context.getSource().getPlayer().setStackInHand(Hand.MAIN_HAND, newBlueprint);
                        return 1;
                    }))));
            commandDispatcher.register(CommandManager.literal("addexampleblueprint")
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty()) {
                            context.getSource().sendError(Text.literal("No item in main hand!"));
                            return 0;
                        }

                        Identifier base = Identifier.ofVanilla("diamond_block");

                        Map<Identifier, Integer> ingredients = new HashMap<>();
                        ingredients.put(Identifier.ofVanilla("diamond"), 100);
                        ingredients.put(Identifier.ofVanilla("dirt"), 50);
                        ingredients.put(Identifier.ofVanilla("short_grass"), 3);
                        ingredients.put(Identifier.ofVanilla("grass_block"), 3);
                        ingredients.put(Identifier.ofVanilla("gold_ingot"), 3);
                        ingredients.put(Identifier.ofVanilla("iron_ingot"), 3);
                        ingredients.put(Identifier.ofVanilla("copper_ingot"), 3);
                        ingredients.put(Identifier.ofVanilla("gold_block"), 3);
                        ingredients.put(Identifier.ofVanilla("iron_block"), 3);
                        ingredients.put(Identifier.ofVanilla("copper_block"), 3);
                        ingredients.put(Identifier.ofVanilla("gold_nugget"), 3);
                        ingredients.put(Identifier.ofVanilla("iron_nugget"), 3);
                        ingredients.put(Identifier.ofVanilla("sand"), 3);
                        ingredients.put(Identifier.ofVanilla("gravel"), 3);
                        ingredients.put(Identifier.ofVanilla("blaze_rod"), 3);
                        ingredients.put(Identifier.ofVanilla("netherrack"), 3);
                        ingredients.put(Identifier.ofVanilla("snowball"), 3);
                        ingredients.put(Identifier.ofVanilla("pumpkin"), 3);
                        ingredients.put(Identifier.ofVanilla("melon"), 3);
                        ingredients.put(Identifier.ofVanilla("tall_grass"), 3);
                        ingredients.put(Identifier.ofVanilla("golden_apple"), 3);
                        ingredients.put(Identifier.ofVanilla("wheat_seeds"), 3);
                        ingredients.put(Identifier.ofVanilla("brick"), 3);
                        ingredients.put(Identifier.ofVanilla("nether_brick"), 3);
                        ingredients.put(Identifier.ofVanilla("chest"), 3);

                        Identifier result = Identifier.ofVanilla("netherite_block");

                        BlueprintBuilderRecipe recipe = new BlueprintBuilderRecipe(base, ingredients, result);
                        stack.set(ModComponents.BLUEPRINT, recipe);
                        return 1;
                    }));
            commandDispatcher.register(CommandManager.literal("removeblueprint")
                    .executes(context -> {
                        ItemStack stack = context.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                        if (stack.isEmpty()) {
                            context.getSource().sendError(Text.literal("No item in main hand!"));
                            return 0;
                        }
                        stack.remove(ModComponents.BLUEPRINT);
                        return 1;
                    }));
        }));
	}
}