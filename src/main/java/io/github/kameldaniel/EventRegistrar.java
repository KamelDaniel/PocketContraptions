package io.github.kameldaniel;

import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.item.PocketContraptionItem;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

public class EventRegistrar {
    public static void init() {
        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, oldStack, newStack) -> {
            if (slot == EquipmentSlot.HEAD && entity instanceof ServerPlayerEntity player) {
                if (oldStack.getItem() instanceof PocketContraptionItem) {
                    setGhost(player, oldStack, false);
                }
                if (newStack.getItem() instanceof PocketContraptionItem) {
                    setGhost(player, newStack, true);
                }
            }
        });

        ServerPlayerEvents.JOIN.register(player -> {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
            if (stack.getItem() instanceof PocketContraptionItem)
                setGhost(player, stack, true);
        });

        ServerPlayerEvents.LEAVE.register(player -> {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
            if (stack.getItem() instanceof PocketContraptionItem)
                setGhost(player, stack, false);
        });
    }

    public static void setGhost(ServerPlayerEntity player, ItemStack pocketContraption, boolean isGhost) {
        if (!pocketContraption.contains(ModComponents.DIM_KEY))
            pocketContraption.set(ModComponents.DIM_KEY, PocketDimension.createPocketDimension(player.getServer()));
        if (isGhost) {
            GhostCamera camera = new GhostCamera(GHOST_CAMERA, player.getWorld());
            camera.refreshPositionAndAngles(
                    player.getX(),
                    player.getY() + 5,
                    player.getZ(),
                    player.getHeadYaw(),
                    player.getPitch());

            player.getWorld().spawnEntity(camera);
            player.setCameraEntity(camera);
        } else {
            Entity oldCamera = player.getCameraEntity();
            player.setCameraEntity(player);
            oldCamera.discard();
        }
    }

    private static final EntityType<GhostCamera> GHOST_CAMERA = Registry.register(
            Registries.ENTITY_TYPE,
            PocketContraptions.id("ghost_camera"),
            EntityType.Builder.create(GhostCamera::new, SpawnGroup.MISC)
                    .dimensions(0.6f, 1.8f)
                    .makeFireImmune()
                    .dropsNothing()
                    .spawnableFarFromPlayer()
                    .disableSummon()
                    .maxTrackingRange(0)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, PocketContraptions.id("ghost_camera")))
    );

    private static class GhostCamera extends LivingEntity {
        public GhostCamera(EntityType<GhostCamera> type, World world) {
            super(type, world);
            super.noClip = true;
            super.setInvisible(true);
            super.setInvulnerable(true);
            super.setNoGravity(true);
        }

        @Override
        public Arm getMainArm() {
            return null;
        }
    }
}
