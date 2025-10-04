package io.github.kameldaniel;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class ModData extends PersistentState {
    public int nextDimID = 0;
    Stack<Integer> unreachableIDs = new Stack<>();

    private ModData() {}

    private ModData(String data) {
        Arrays.stream(data.split("\\s+")).mapToInt(Integer::valueOf).forEach(unreachableIDs::push);
        this.nextDimID = this.unreachableIDs.pop();
    }

    private String getSaveString() {
        StringBuilder saveString = new StringBuilder();
        while (!this.unreachableIDs.empty()) {
            saveString.append(this.unreachableIDs.pop()).append(" ");
        }
        saveString.append(this.nextDimID);
        return saveString.toString();
    }

    private static final Codec<ModData> CODEC = Codec.STRING.fieldOf("dim_ids").codec().xmap(
            ModData::new,
            ModData::getSaveString
    );

    private static final PersistentStateType<ModData> type = new PersistentStateType<>(
            PocketContraptions.MOD_ID,
            ModData::new,
            CODEC,
            null
    );

    public static ModData getModData(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        Objects.requireNonNull(world);
        ModData modData = world.getPersistentStateManager().getOrCreate(ModData.type);
        modData.markDirty();
        return modData;
    }

    public int getDimID() {
        if (this.unreachableIDs.empty()) return nextDimID++;
        else return this.unreachableIDs.pop();
    }

    public void addUnreachable(int unreachableID) {
        this.unreachableIDs.push(unreachableID);
    }
}