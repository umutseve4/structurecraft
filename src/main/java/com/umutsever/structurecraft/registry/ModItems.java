package com.umutsever.structurecraft.registry;

import com.umutsever.structurecraft.StructureCraft;
import com.umutsever.structurecraft.gen.CozyStarterHome;
import com.umutsever.structurecraft.gen.LateGameFortress;
import com.umutsever.structurecraft.gen.MegaCity;
import com.umutsever.structurecraft.item.BlueprintItem;
import com.umutsever.structurecraft.item.VehicleItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item COZY_HOME_BLUEPRINT = register("cozy_home_blueprint",
            new BlueprintItem(new Item.Settings().maxCount(16), CozyStarterHome::generate, "Cozy Starter Home"));

    public static final Item FORTRESS_BLUEPRINT = register("fortress_blueprint",
            new BlueprintItem(new Item.Settings().maxCount(16), LateGameFortress::generate, "Late-Game Fortress"));

    public static final Item MEGA_CITY_BLUEPRINT = register("mega_city_blueprint",
            new BlueprintItem(new Item.Settings().maxCount(16), MegaCity::generate, "Mega City"));

    public static final Item SKATEBOARD = register("skateboard",
            new VehicleItem(new Item.Settings().maxCount(1), () -> ModEntities.SKATEBOARD));

    public static final Item AIRPLANE = register("airplane",
            new VehicleItem(new Item.Settings().maxCount(1), () -> ModEntities.AIRPLANE));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(StructureCraft.MOD_ID, name), item);
    }

    public static void register() {
        // static init
    }

    private ModItems() {}
}
