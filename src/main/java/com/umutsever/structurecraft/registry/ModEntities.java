package com.umutsever.structurecraft.registry;

import com.umutsever.structurecraft.StructureCraft;
import com.umutsever.structurecraft.entity.AirplaneEntity;
import com.umutsever.structurecraft.entity.SkateboardEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
    public static final EntityType<SkateboardEntity> SKATEBOARD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(StructureCraft.MOD_ID, "skateboard"),
            FabricEntityTypeBuilder.<SkateboardEntity>create(SpawnGroup.MISC, SkateboardEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.25f))
                    .trackRangeBlocks(64)
                    .build());

    public static final EntityType<AirplaneEntity> AIRPLANE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(StructureCraft.MOD_ID, "airplane"),
            FabricEntityTypeBuilder.<AirplaneEntity>create(SpawnGroup.MISC, AirplaneEntity::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 1.2f))
                    .trackRangeBlocks(128)
                    .build());

    public static void register() {
        // static init
    }

    private ModEntities() {}
}
