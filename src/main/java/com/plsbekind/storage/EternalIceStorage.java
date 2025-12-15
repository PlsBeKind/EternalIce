package com.plsbekind.storage;

import com.plsbekind.NoIceMelt;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EternalIceStorage {
    private static final String DATA_NAME = "noicemelt_eternal_ice.dat";
    private static final ConcurrentHashMap<ServerLevel, EternalIceStorage> INSTANCES = new ConcurrentHashMap<>();
    private final Set<BlockPos> eternalIcePositions = new HashSet<>();
    private final ServerLevel world;

    private EternalIceStorage(ServerLevel world) {
        this.world = world;
    }

    public static void init() {
        // Load data when server starts/worlds load
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerLevel world : server.getAllLevels()) {
                EternalIceStorage storage = new EternalIceStorage(world);
                storage.load();
                INSTANCES.put(world, storage);
            }
        });

        // Save data when server stops
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (EternalIceStorage storage : INSTANCES.values()) {
                storage.save();
            }
            INSTANCES.clear();
        });
    }

    public static EternalIceStorage get(ServerLevel world) {
        return INSTANCES.computeIfAbsent(world, w -> {
            EternalIceStorage storage = new EternalIceStorage(w);
            storage.load();
            return storage;
        });
    }

    public void addEternalIce(BlockPos pos) {
        eternalIcePositions.add(pos.immutable());
        save(); // Auto-save on change
        NoIceMelt.LOGGER.info("Added eternal ice at {}. Total: {}", pos, eternalIcePositions.size());
    }

    public void removeEternalIce(BlockPos pos) {
        eternalIcePositions.remove(pos);
        save(); // Auto-save on change
        NoIceMelt.LOGGER.info("Removed eternal ice from {}. Total: {}", pos, eternalIcePositions.size());
    }

    public boolean isEternalIce(BlockPos pos) {
        return eternalIcePositions.contains(pos);
    }

    private File getDataFile() {
        MinecraftServer server = world.getServer();
        // Get the world save directory
        File worldSaveDir = server.getServerDirectory().resolve("world").toFile();
        File dataDir = new File(worldSaveDir, "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        String dimensionName = world.dimension().location().getPath();
        return new File(dataDir, DATA_NAME.replace(".dat", "_" + dimensionName + ".dat"));
    }

    private void save() {
        try {
            CompoundTag nbt = new CompoundTag();
            ListTag list = new ListTag();
            for (BlockPos pos : eternalIcePositions) {
                CompoundTag posNbt = new CompoundTag();
                posNbt.putInt("x", pos.getX());
                posNbt.putInt("y", pos.getY());
                posNbt.putInt("z", pos.getZ());
                list.add(posNbt);
            }
            nbt.put("positions", list);
            
            File file = getDataFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                net.minecraft.nbt.NbtIo.writeCompressed(nbt, fos);
            }
            NoIceMelt.LOGGER.info("Saved {} eternal ice positions to {}", eternalIcePositions.size(), file.getAbsolutePath());
        } catch (Exception e) {
            NoIceMelt.LOGGER.error("Failed to save eternal ice data", e);
        }
    }

    private void load() {
        try {
            File file = getDataFile();
            if (!file.exists()) {
                NoIceMelt.LOGGER.info("No saved data found at {}", file.getAbsolutePath());
                return;
            }

            CompoundTag nbt;
            try (FileInputStream fis = new FileInputStream(file)) {
                nbt = net.minecraft.nbt.NbtIo.readCompressed(fis, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
            }

            eternalIcePositions.clear();
            ListTag list = (ListTag) nbt.get("positions");
            if (list != null) {
                for (Tag tag : list) {
                    if (tag instanceof CompoundTag posNbt) {
                        int x = posNbt.getInt("x").orElse(0);
                        int y = posNbt.getInt("y").orElse(0);
                        int z = posNbt.getInt("z").orElse(0);
                        BlockPos pos = new BlockPos(x, y, z);
                        eternalIcePositions.add(pos);
                    }
                }
            }
            NoIceMelt.LOGGER.info("Loaded {} eternal ice positions from {}", eternalIcePositions.size(), file.getAbsolutePath());
        } catch (Exception e) {
            NoIceMelt.LOGGER.error("Failed to load eternal ice data", e);
        }
    }
}
