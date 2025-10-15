package io.github.kameldaniel;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.Objects;

public class ModData extends PersistentState {
    public int nextDimID;

    /**
     * Constructor for a new world that does not yet have ModData stored.
     */
    private ModData() {
        this.nextDimID = 0;
    }

    /**
     * Constructor for ModData in a server that already has ModData stored.
     * @param nextDimID
     * The value stored in the server
     */
    private ModData(int nextDimID) {
        this.nextDimID = nextDimID;
    }

    /**
     * Access the variable that is saved on the server
     * @return
     * nextDimID in ModData
     */
    private int getSaveData() {
        return this.nextDimID;
    }

    // The Codec that stores nextDimID on the server
    private static final Codec<ModData> CODEC = Codec.INT.fieldOf("next_dim_id").codec().xmap(
            ModData::new,
            ModData::getSaveData
    );

    // The PersistentStateType that the server checks for to initialize/retrieve.
    private static final PersistentStateType<ModData> type = new PersistentStateType<>(
            PocketContraptions.MOD_ID,
            ModData::new,
            CODEC,
            null
    );

    /**
     * Access pocket-contraptions data stored on the server
     * @param server
     * the server to get ModData from
     * @return
     * the ModData object with the saved values.
     */
    public static ModData getModData(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        Objects.requireNonNull(world);
        return world.getPersistentStateManager().getOrCreate(ModData.type);
    }

    /**
     * get the id that should be given to the next dimension created and increment it for next time.
     * @return
     * the id of the next pocket dimension
     */
    public int getDimID() {
        super.markDirty();
        return nextDimID++;
    }
}