package com.umutsever.structurecraft.gen;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static com.umutsever.structurecraft.gen.BuildHelper.*;

/**
 * Cozy survival cottage: fireplace + chimney, furnished interior, stocked chests,
 * crop farm with scarecrow, water well, storage shed, fenced yard with gates.
 */
public final class CozyStarterHome {

    public static void generate(ServerWorld w, BlockPos origin, Direction facing) {
        Frame fr = new Frame(origin, facing);

        clearArea(w, fr);
        yard(w, fr);
        cottage(w, fr);
        interior(w, fr);
        farm(w, fr);
        well(w, fr);
        shed(w, fr);
    }

    private static void clearArea(ServerWorld w, Frame fr) {
        // Clear air space and lay grass ground across the plot.
        fill(w, fr.at(-13, 0, -1), fr.at(13, 12, 22), Blocks.AIR);
        fill(w, fr.at(-13, -1, -1), fr.at(13, -1, 22), Blocks.GRASS_BLOCK);
    }

    private static void yard(ServerWorld w, Frame fr) {
        BlockState fence = Blocks.OAK_FENCE.getDefaultState();
        // Perimeter fence at ground level.
        for (int r = -13; r <= 13; r++) {
            set(w, fr.at(r, 0, -1), fence);
            set(w, fr.at(r, 0, 22), fence);
        }
        for (int f = -1; f <= 22; f++) {
            set(w, fr.at(-13, 0, f), fence);
            set(w, fr.at(13, 0, f), fence);
        }
        // Front gate + side gate.
        set(w, fr.at(0, 0, -1), Blocks.OAK_FENCE_GATE.getDefaultState().with(FenceGateBlock.FACING, fr.forward()));
        set(w, fr.at(13, 0, 10), Blocks.OAK_FENCE_GATE.getDefaultState().with(FenceGateBlock.FACING, fr.right()));
        // Lanterns on the four fence corners.
        for (BlockPos p : new BlockPos[]{fr.at(-13, 1, -1), fr.at(13, 1, -1), fr.at(-13, 1, 22), fr.at(13, 1, 22)}) {
            set(w, p, Blocks.LANTERN);
        }
        // Cobblestone path gate -> door.
        for (int f = 0; f <= 4; f++) set(w, fr.at(0, -1, f), Blocks.COBBLESTONE);
    }

    private static void cottage(ServerWorld w, Frame fr) {
        // Footprint r[-4..4], f[5..13]; floor y0, walls y1..4.
        fill(w, fr.at(-4, 0, 5), fr.at(4, 0, 13), Blocks.SPRUCE_PLANKS);
        walls(w, fr.at(-4, 1, 5), fr.at(4, 4, 13), Blocks.OAK_PLANKS.getDefaultState());
        // Log corner posts.
        for (int[] c : new int[][]{{-4, 5}, {4, 5}, {-4, 13}, {4, 13}}) {
            fill(w, fr.at(c[0], 1, c[1]), fr.at(c[0], 4, c[1]), Blocks.OAK_LOG);
        }
        // Windows.
        BlockState pane = Blocks.GLASS_PANE.getDefaultState();
        for (int r : new int[]{-2, 2}) { set(w, fr.at(r, 2, 5), pane); set(w, fr.at(r, 3, 5), pane); }
        for (int f : new int[]{7, 9, 11}) {
            set(w, fr.at(-4, 2, f), pane); set(w, fr.at(-4, 3, f), pane);
            set(w, fr.at(4, 2, f), pane);  set(w, fr.at(4, 3, f), pane);
        }
        // Front door (faces the player).
        BlockState doorLower = Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, fr.back()).with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        set(w, fr.at(0, 1, 5), doorLower);
        set(w, fr.at(0, 2, 5), doorLower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        set(w, fr.at(0, 0, 4), Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, fr.forward()));
        // Pitched roof (stairs left/right, slab ridge), overhanging by 1.
        BlockState roofL = Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, fr.right());
        BlockState roofR = Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, fr.left());
        for (int i = 0; i <= 4; i++) {
            for (int f = 4; f <= 14; f++) {
                if (i < 4) {
                    set(w, fr.at(-5 + i, 4 + i, f), roofL);
                    set(w, fr.at(5 - i, 4 + i, f), roofR);
                } else {
                    set(w, fr.at(0, 8, f), Blocks.DARK_OAK_SLAB.getDefaultState());
                }
            }
        }
        // Close the gable triangles.
        for (int i = 1; i <= 3; i++) {
            fill(w, fr.at(-4 + i, 4 + i, 5), fr.at(4 - i, 4 + i, 5), Blocks.OAK_PLANKS);
            fill(w, fr.at(-4 + i, 4 + i, 13), fr.at(4 - i, 4 + i, 13), Blocks.OAK_PLANKS);
        }
        // Chimney through the roof at the back-left.
        fill(w, fr.at(-3, 1, 12), fr.at(-3, 9, 12), Blocks.BRICKS);
        set(w, fr.at(-3, 10, 12), Blocks.BRICK_WALL);
    }

    private static void interior(ServerWorld w, Frame fr) {
        // Fireplace against the back wall (brick hearth + lit campfire).
        fill(w, fr.at(-4, 1, 11), fr.at(-2, 3, 12), Blocks.BRICKS);
        fill(w, fr.at(-3, 1, 11), fr.at(-3, 2, 11), Blocks.AIR);
        set(w, fr.at(-3, 0, 11), Blocks.BRICKS);
        set(w, fr.at(-3, 1, 11), Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, true));
        // Furnace + crafting table on the right wall.
        set(w, fr.at(3, 1, 12), Blocks.FURNACE.getDefaultState().with(AbstractFurnaceBlock.FACING, fr.back()));
        set(w, fr.at(2, 1, 12), Blocks.CRAFTING_TABLE);
        // Bed on the left wall.
        BlockState foot = Blocks.RED_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedBlock.PART, BedPart.FOOT);
        set(w, fr.at(-3, 1, 6), foot);
        set(w, fr.at(-3, 1, 7), foot.with(BedBlock.PART, BedPart.HEAD));
        // Two stocked chests on the right side.
        BlockState chest = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, fr.left());
        set(w, fr.at(3, 1, 6), chest);
        fillContainer(w, fr.at(3, 1, 6),
                new ItemStack(Items.BREAD, 16), new ItemStack(Items.TORCH, 32),
                new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.IRON_AXE), new ItemStack(Items.SHIELD));
        set(w, fr.at(3, 1, 8), chest);
        fillContainer(w, fr.at(3, 1, 8),
                new ItemStack(Items.OAK_SAPLING, 8), new ItemStack(Items.WHEAT_SEEDS, 16),
                new ItemStack(Items.COAL, 24), new ItemStack(Items.OAK_LOG, 32),
                new ItemStack(Items.COBBLESTONE, 64), new ItemStack(Items.STRING, 8));
        // Light + rug.
        set(w, fr.at(0, 4, 9), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
        fill(w, fr.at(-1, 1, 8), fr.at(1, 1, 10), Blocks.RED_CARPET);
    }

    private static void farm(ServerWorld w, Frame fr) {
        // 5x5 farm to the right of the cottage: farmland, center water, wheat.
        BlockState farmland = Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7);
        BlockState wheat = Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, 7);
        for (int r = 7; r <= 11; r++) {
            for (int f = 5; f <= 9; f++) {
                if (r == 9 && f == 7) {
                    set(w, fr.at(r, -1, f), Blocks.WATER);
                } else {
                    set(w, fr.at(r, -1, f), farmland);
                    set(w, fr.at(r, 0, f), wheat);
                }
            }
        }
        // Scarecrow: hay base + armor stand wearing a carved pumpkin.
        set(w, fr.at(11, 0, 10), Blocks.HAY_BLOCK);
        Vec3d sp = Vec3d.ofBottomCenter(fr.at(11, 1, 10));
        ArmorStandEntity stand = new ArmorStandEntity(EntityType.ARMOR_STAND, w);
        stand.refreshPositionAndAngles(sp.x, sp.y, sp.z, fr.back().asRotation(), 0f);
        stand.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));
        stand.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
        w.spawnEntity(stand);
    }

    private static void well(ServerWorld w, Frame fr) {
        // Water well to the left of the cottage.
        int cr = -9, cf = 7;
        fill(w, fr.at(cr - 1, 0, cf - 1), fr.at(cr + 1, 0, cf + 1), Blocks.COBBLESTONE);
        set(w, fr.at(cr, -1, cf), Blocks.WATER);
        set(w, fr.at(cr, 0, cf), Blocks.WATER);
        for (int[] c : new int[][]{{cr - 1, cf - 1}, {cr + 1, cf - 1}, {cr - 1, cf + 1}, {cr + 1, cf + 1}}) {
            fill(w, fr.at(c[0], 1, c[1]), fr.at(c[0], 2, c[1]), Blocks.OAK_FENCE);
        }
        fill(w, fr.at(cr - 1, 3, cf - 1), fr.at(cr + 1, 3, cf + 1), Blocks.COBBLESTONE_SLAB);
    }

    private static void shed(ServerWorld w, Frame fr) {
        // Small storage shed behind the cottage: r[-9..-5], f[16..20].
        fill(w, fr.at(-9, 0, 16), fr.at(-5, 0, 20), Blocks.SPRUCE_PLANKS);
        walls(w, fr.at(-9, 1, 16), fr.at(-5, 3, 20), Blocks.SPRUCE_PLANKS.getDefaultState());
        fill(w, fr.at(-10, 4, 15), fr.at(-4, 4, 21), Blocks.SPRUCE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM));
        BlockState doorLower = Blocks.SPRUCE_DOOR.getDefaultState().with(DoorBlock.FACING, fr.back()).with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        set(w, fr.at(-7, 1, 16), doorLower);
        set(w, fr.at(-7, 2, 16), doorLower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        set(w, fr.at(-6, 1, 19), Blocks.BARREL);
        fillContainer(w, fr.at(-6, 1, 19), new ItemStack(Items.BONE_MEAL, 16), new ItemStack(Items.CARROT, 8), new ItemStack(Items.POTATO, 8));
        set(w, fr.at(-8, 1, 19), Blocks.BARREL);
        fillContainer(w, fr.at(-8, 1, 19), new ItemStack(Items.LADDER, 8), new ItemStack(Items.OAK_FENCE, 16), new ItemStack(Items.TORCH, 16));
        set(w, fr.at(-7, 3, 18), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
    }

    private CozyStarterHome() {}
}
