package io.github.kameldaniel.item;

import io.github.kameldaniel.ModData;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class QuantumCore extends Item {
    public QuantumCore(Settings settings) {
        super(settings);
    }

    /**
     * Bounds the QuantumCore to a new pocket dimension if not already bound,
     * Teleports the player to the bound pocket dimension.
     * If the QuantumCore is bound to a pocket dimension that does not exist, the player is notified.
     * @param world
     * the World the QuantumCore is used in
     * @param player
     * the PlayerEntity that uses the QuantumCore
     * @param hand
     * the Hand the QuantumCore is in when it is used
     * @return
     * ActionResult.PASS if the given world is a client;
     * ActionResult.SUCCESS if the player is successfully teleported to a pocket dimension;
     * ActionResult.FAIL if the QuantumCore is bound to a pocket dimension that does not exist;
     */
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) return ActionResult.PASS;
        Objects.requireNonNull(world.getServer());
        ItemStack stack = player.getStackInHand(hand);
        try {
            // Get the dimID of the attached pocket dimension or throw a NullPointerException if unbound.
            int dimID = Objects.requireNonNull(stack.get(ModComponents.DIM_KEY));

            // Make sure the dimID is valid; if not, change to valid id
            if (dimID >= ModData.getModData(world.getServer()).nextDimID)
                stack.set(ModComponents.DIM_KEY, PocketDimension.createPocketDimension(world.getServer()));
            // Teleport the player to 0 68 0 in the pocket dimension
            Set<PositionFlag> flags = Collections.emptySet();
            player.teleport(PocketDimension.getDim(player.getServer(), dimID),
                    0, 68, 0, flags, 0.0f, 0.0f, false);

            // Player has been teleported, Action was successful
            return ActionResult.SUCCESS;
        } catch (NullPointerException e) {
            // Unbound to dimension; bound to new dimension and try again
            stack.set(ModComponents.DIM_KEY, PocketDimension.createPocketDimension(world.getServer()));
            return this.use(world, player, hand);
        }
    }
}