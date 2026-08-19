package com.umutsever.structurecraft;

import com.umutsever.structurecraft.registry.ModEntities;
import com.umutsever.structurecraft.registry.ModItemGroups;
import com.umutsever.structurecraft.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructureCraft implements ModInitializer {
    public static final String MOD_ID = "structurecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntities.register();
        ModItems.register();
        ModItemGroups.register();
        LOGGER.info("StructureCraft initialized: 3 blueprints, 2 vehicles.");
    }
}
