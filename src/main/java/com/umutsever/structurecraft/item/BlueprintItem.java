package com.umutsever.structurecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

/**
 * Generic blueprint item. Right-click on the ground -> the wired generator
 * builds the structure with its entrance facing the player.
 */
public class BlueprintItem extends Item {

    @FunctionalInterface
    public interface StructureGenerator {
        void generate(ServerWorld world, BlockPos origin, Direction facing);
    }

    private final StructureGenerator generator;
    private final String structureName;

    public BlueprintItem(Settings settings, StructureGenerator generator, String structureName) {
        super(settings);
        this.generator = generator;
        this.structureName = structureName;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockPos origin = context.getBlockPos().up();
        Direction facing = context.getHorizontalPlayerFacing();
        PlayerEntity player = context.getPlayer();

        generator.generate((ServerWorld) world, origin, facing);

        if (player != null) {
            player.sendMessage(Text.literal(structureName + " generated!").formatted(Formatting.GREEN), true);
            if (!player.getAbilities().creativeMode) {
                context.getStack().decrement(1);
            }
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Right-click the ground to build: " + structureName).formatted(Formatting.GRAY));
    }
}
