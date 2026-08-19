package com.umutsever.structurecraft.registry;

import com.umutsever.structurecraft.StructureCraft;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    public static final ItemGroup STRUCTURECRAFT = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.COZY_HOME_BLUEPRINT))
            .displayName(Text.translatable("itemGroup.structurecraft.main"))
            .entries((context, entries) -> {
                entries.add(ModItems.COZY_HOME_BLUEPRINT);
                entries.add(ModItems.FORTRESS_BLUEPRINT);
                entries.add(ModItems.MEGA_CITY_BLUEPRINT);
                entries.add(ModItems.SKATEBOARD);
                entries.add(ModItems.AIRPLANE);
            })
            .build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(StructureCraft.MOD_ID, "main"), STRUCTURECRAFT);
    }

    private ModItemGroups() {}
}
