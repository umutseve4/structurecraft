package com.umutsever.structurecraft.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Small imperative building toolkit used by all generators.
 * Coordinates are absolute BlockPos; generators compute them from an origin + facing rotation.
 */
public final class BuildHelper {
    public static final int FLAGS = Block.NOTIFY_ALL;

    public static void set(ServerWorld w, BlockPos p, BlockState s) {
        w.setBlockState(p, s, FLAGS);
    }

    public static void set(ServerWorld w, BlockPos p, Block b) {
        set(w, p, b.getDefaultState());
    }

    /** Fill an inclusive cuboid between two corners (any order). */
    public static void fill(ServerWorld w, BlockPos a, BlockPos b, BlockState s) {
        int x1 = Math.min(a.getX(), b.getX()), x2 = Math.max(a.getX(), b.getX());
        int y1 = Math.min(a.getY(), b.getY()), y2 = Math.max(a.getY(), b.getY());
        int z1 = Math.min(a.getZ(), b.getZ()), z2 = Math.max(a.getZ(), b.getZ());
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                for (int z = z1; z <= z2; z++)
                    w.setBlockState(m.set(x, y, z), s, FLAGS);
    }

    public static void fill(ServerWorld w, BlockPos a, BlockPos b, Block block) {
        fill(w, a, b, block.getDefaultState());
    }

    /** Cuboid shell: fills only the 6 faces (walls, floor, ceiling). */
    public static void hollowBox(ServerWorld w, BlockPos a, BlockPos b, BlockState s) {
        int x1 = Math.min(a.getX(), b.getX()), x2 = Math.max(a.getX(), b.getX());
        int y1 = Math.min(a.getY(), b.getY()), y2 = Math.max(a.getY(), b.getY());
        int z1 = Math.min(a.getZ(), b.getZ()), z2 = Math.max(a.getZ(), b.getZ());
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                for (int z = z1; z <= z2; z++)
                    if (x == x1 || x == x2 || y == y1 || y == y2 || z == z1 || z == z2)
                        w.setBlockState(m.set(x, y, z), s, FLAGS);
    }

    /** Only the 4 vertical walls (no floor/ceiling). */
    public static void walls(ServerWorld w, BlockPos a, BlockPos b, BlockState s) {
        int x1 = Math.min(a.getX(), b.getX()), x2 = Math.max(a.getX(), b.getX());
        int y1 = Math.min(a.getY(), b.getY()), y2 = Math.max(a.getY(), b.getY());
        int z1 = Math.min(a.getZ(), b.getZ()), z2 = Math.max(a.getZ(), b.getZ());
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                for (int z = z1; z <= z2; z++)
                    if (x == x1 || x == x2 || z == z1 || z == z2)
                        w.setBlockState(m.set(x, y, z), s, FLAGS);
    }

    /** Put item stacks into a chest/barrel-like container that was just placed. */
    public static void fillContainer(ServerWorld w, BlockPos p, ItemStack... stacks) {
        BlockEntity be = w.getBlockEntity(p);
        if (be instanceof LootableContainerBlockEntity container) {
            for (int i = 0; i < stacks.length && i < container.size(); i++) {
                container.setStack(i, stacks[i]);
            }
            container.markDirty();
        }
    }

    private BuildHelper() {}
}
