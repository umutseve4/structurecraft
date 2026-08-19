package com.umutsever.structurecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

/** Spawns the associated vehicle entity where the player clicks. */
public class VehicleItem extends Item {
    private final Supplier<EntityType<? extends Entity>> typeSupplier;

    public VehicleItem(Settings settings, Supplier<EntityType<? extends Entity>> typeSupplier) {
        super(settings);
        this.typeSupplier = typeSupplier;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockPos pos = context.getBlockPos().up();
        Entity entity = typeSupplier.get().create(world);
        if (entity == null) {
            return ActionResult.FAIL;
        }
        Vec3d spawn = Vec3d.ofBottomCenter(pos);
        float yaw = context.getPlayer() != null ? context.getPlayer().getYaw() : 0f;
        entity.refreshPositionAndAngles(spawn.x, spawn.y, spawn.z, yaw, 0f);
        world.spawnEntity(entity);
        if (context.getPlayer() != null && !context.getPlayer().getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }
        return ActionResult.CONSUME;
    }
}
