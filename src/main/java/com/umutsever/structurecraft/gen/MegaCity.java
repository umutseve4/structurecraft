package com.umutsever.structurecraft.gen;

import com.umutsever.structurecraft.registry.ModEntities;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import static com.umutsever.structurecraft.gen.BuildHelper.*;

/**
 * 129x129 layered city. Generated in explicit layers:
 *   1) ground plate  2) road grid + railway  3) perimeter wall  4) district lots.
 * Lots (4x4 grid) rotate through: skyscraper, apartments, houses, industrial,
 * construction site, park, airport, plaza.
 */
public final class MegaCity {
    private static final int HALF = 64;        // city spans r[-64..64], f[0..128]
    private static final int LOT = 32;         // road every 32 blocks

    public static void generate(ServerWorld w, BlockPos origin, Direction facing) {
        Frame fr = new Frame(origin, facing);
        Random random = Random.create(origin.asLong());

        // Layer 1: clear + ground plate.
        fill(w, fr.at(-HALF, 0, 0), fr.at(HALF, 50, 2 * HALF), Blocks.AIR);
        fill(w, fr.at(-HALF, -1, 0), fr.at(HALF, -1, 2 * HALF), Blocks.LIGHT_GRAY_CONCRETE);

        // Layer 2: roads + railway.
        roads(w, fr);
        railway(w, fr);

        // Layer 3: perimeter wall with road openings.
        perimeterWall(w, fr);

        // Layer 4: districts. 4x4 lots, each lot interior is 27x27.
        int lotIndex = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int r0 = -HALF + 3 + i * LOT;   // lot min corner (right axis)
                int f0 = 3 + j * LOT;           // lot min corner (forward axis)
                switch (lotIndex % 8) {
                    case 0 -> skyscraper(w, fr, r0, f0, random);
                    case 1 -> apartments(w, fr, r0, f0);
                    case 2 -> houses(w, fr, r0, f0);
                    case 3 -> industrial(w, fr, r0, f0);
                    case 4 -> constructionSite(w, fr, r0, f0);
                    case 5 -> park(w, fr, r0, f0);
                    case 6 -> airport(w, fr, r0, f0);
                    case 7 -> skyscraper(w, fr, r0, f0, random);
                }
                lotIndex++;
            }
        }
    }

    // ------------------------------------------------------------------ infra
    private static void roads(ServerWorld w, Frame fr) {
        BlockState asphalt = Blocks.GRAY_CONCRETE.getDefaultState();
        BlockState stripe = Blocks.WHITE_CONCRETE.getDefaultState();
        for (int k = -HALF; k <= HALF; k += LOT) {
            // Roads parallel to forward axis.
            fill(w, fr.at(k - 2, -1, 0), fr.at(k + 2, -1, 2 * HALF), asphalt);
            for (int f = 0; f <= 2 * HALF; f += 4) set(w, fr.at(k, -1, f), stripe);
            // Roads parallel to right axis.
            fill(w, fr.at(-HALF, -1, k + HALF - 2), fr.at(HALF, -1, k + HALF + 2), asphalt);
            for (int r = -HALF; r <= HALF; r += 4) set(w, fr.at(r, -1, k + HALF), stripe);
            // Street lights along forward roads.
            for (int f = 8; f < 2 * HALF; f += 16) {
                streetLight(w, fr, k + 3, f);
            }
        }
    }

    private static void streetLight(ServerWorld w, Frame fr, int r, int f) {
        fill(w, fr.at(r, 0, f), fr.at(r, 4, f), Blocks.POLISHED_BLACKSTONE_WALL);
        set(w, fr.at(r, 5, f), Blocks.GLOWSTONE);
    }

    private static void railway(ServerWorld w, Frame fr) {
        // Elevated-free straight railway line next to the r=-32 road.
        int r = -LOT + 4;
        for (int f = 1; f < 2 * HALF; f++) {
            set(w, fr.at(r, -1, f), Blocks.GRAVEL);
            set(w, fr.at(r, 0, f), (f % 16 == 0 ? Blocks.POWERED_RAIL : Blocks.RAIL).getDefaultState());
            if (f % 16 == 0) set(w, fr.at(r + 1, 0, f), Blocks.REDSTONE_TORCH);
        }
        // Small station platform.
        fill(w, fr.at(r + 1, -1, 60), fr.at(r + 3, -1, 68), Blocks.SMOOTH_STONE);
        fill(w, fr.at(r + 3, 0, 60), fr.at(r + 3, 0, 68), Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM));
    }

    private static void perimeterWall(ServerWorld w, Frame fr) {
        BlockState wall = Blocks.STONE_BRICKS.getDefaultState();
        for (int r = -HALF; r <= HALF; r++) {
            if (Math.abs(r % LOT) > 3) {
                fill(w, fr.at(r, 0, 0), fr.at(r, 5, 0), wall);
                fill(w, fr.at(r, 0, 2 * HALF), fr.at(r, 5, 2 * HALF), wall);
            }
        }
        for (int f = 0; f <= 2 * HALF; f++) {
            if (Math.abs((f - HALF) % LOT) > 3) {
                fill(w, fr.at(-HALF, 0, f), fr.at(-HALF, 5, f), wall);
                fill(w, fr.at(HALF, 0, f), fr.at(HALF, 5, f), wall);
            }
        }
    }

    // -------------------------------------------------------------- districts
    private static void skyscraper(ServerWorld w, Frame fr, int r0, int f0, Random random) {
        int height = 24 + random.nextInt(18);
        int a = r0 + 3, b = f0 + 3;           // 21x21 footprint inside the lot
        BlockState shell = Blocks.CYAN_TERRACOTTA.getDefaultState();
        BlockState glass = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        for (int y = 0; y < height; y++) {
            BlockState s = (y % 4 == 2 || y % 4 == 3) ? glass : shell;
            walls(w, fr.at(a, y, b), fr.at(a + 20, y, b + 20), s);
        }
        // Interior floors every 5 blocks + ladder core.
        for (int y = 4; y < height; y += 5) {
            fill(w, fr.at(a + 1, y, b + 1), fr.at(a + 19, y, b + 19), Blocks.SMOOTH_STONE);
            set(w, fr.at(a + 10, y, b + 10), Blocks.AIR);
            // simple office furniture
            set(w, fr.at(a + 4, y + 1, b + 4), Blocks.CRAFTING_TABLE);
            set(w, fr.at(a + 16, y + 1, b + 16), Blocks.BOOKSHELF);
            set(w, fr.at(a + 10, y + 4 < height ? y + 4 : y + 1, b + 4), Blocks.SEA_LANTERN);
        }
        for (int y = 0; y < height; y++) {
            set(w, fr.at(a + 10, y, b + 11), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.forward()));
        }
        // Roof + antenna.
        fill(w, fr.at(a, height, b), fr.at(a + 20, height, b + 20), Blocks.GRAY_CONCRETE);
        fill(w, fr.at(a + 10, height + 1, b + 10), fr.at(a + 10, height + 6, b + 10), Blocks.IRON_BARS);
        set(w, fr.at(a + 10, height + 7, b + 10), Blocks.REDSTONE_TORCH);
        // Billboard on the front face near the top.
        fill(w, fr.at(a + 6, height - 6, b - 1), fr.at(a + 14, height - 3, b - 1), Blocks.WHITE_CONCRETE);
        fill(w, fr.at(a + 8, height - 5, b - 1), fr.at(a + 12, height - 4, b - 1), Blocks.BLACK_CONCRETE);
        // Lobby doorway.
        fill(w, fr.at(a + 9, 0, b), fr.at(a + 11, 2, b), Blocks.AIR);
    }

    private static void apartments(ServerWorld w, Frame fr, int r0, int f0) {
        for (int k = 0; k < 2; k++) {
            int a = r0 + 3 + k * 12, b = f0 + 4;
            walls(w, fr.at(a, 0, b), fr.at(a + 8, 11, b + 18), Blocks.BRICKS.getDefaultState());
            for (int y = 3; y <= 9; y += 3) fill(w, fr.at(a + 1, y, b + 1), fr.at(a + 7, y, b + 17), Blocks.OAK_PLANKS);
            fill(w, fr.at(a, 12, b), fr.at(a + 8, 12, b + 18), Blocks.STONE_BRICK_SLAB);
            for (int y : new int[]{1, 4, 7, 10})
                for (int f = b + 2; f <= b + 16; f += 3) {
                    set(w, fr.at(a, y + 1, f), Blocks.GLASS_PANE);
                    set(w, fr.at(a + 8, y + 1, f), Blocks.GLASS_PANE);
                }
            fill(w, fr.at(a + 4, 0, b), fr.at(a + 4, 1, b), Blocks.AIR);
            for (int y = 0; y <= 10; y++) set(w, fr.at(a + 4, y, b + 17), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.back()));
            // Beds per floor.
            for (int y : new int[]{1, 4, 7, 10}) {
                set(w, fr.at(a + 2, y, b + 3), Blocks.RED_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedBlock.PART, net.minecraft.block.enums.BedPart.FOOT));
                set(w, fr.at(a + 2, y, b + 4), Blocks.RED_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedBlock.PART, net.minecraft.block.enums.BedPart.HEAD));
                set(w, fr.at(a + 6, y, b + 3), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, fr.forward()));
            }
        }
    }

    private static void houses(ServerWorld w, Frame fr, int r0, int f0) {
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                int a = r0 + 3 + i * 14, b = f0 + 3 + j * 14;
                fill(w, fr.at(a, -1, b), fr.at(a + 8, -1, b + 8), Blocks.GRASS_BLOCK);
                fill(w, fr.at(a + 1, 0, b + 1), fr.at(a + 7, 0, b + 7), Blocks.OAK_PLANKS);
                walls(w, fr.at(a + 1, 1, b + 1), fr.at(a + 7, 3, b + 7), Blocks.WHITE_TERRACOTTA.getDefaultState());
                fill(w, fr.at(a, 4, b), fr.at(a + 8, 4, b + 8), Blocks.DARK_OAK_SLAB);
                fill(w, fr.at(a + 2, 5, b + 2), fr.at(a + 6, 5, b + 6), Blocks.DARK_OAK_SLAB);
                BlockState lower = Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, fr.back()).with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
                set(w, fr.at(a + 4, 1, b + 1), lower);
                set(w, fr.at(a + 4, 2, b + 1), lower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
                set(w, fr.at(a + 2, 2, b + 1), Blocks.GLASS_PANE);
                set(w, fr.at(a + 6, 2, b + 1), Blocks.GLASS_PANE);
                set(w, fr.at(a + 2, 1, b + 6), Blocks.RED_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedBlock.PART, net.minecraft.block.enums.BedPart.FOOT));
                set(w, fr.at(a + 2, 1, b + 7 - 1 + 1 - 1 + 1), Blocks.RED_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedBlock.PART, net.minecraft.block.enums.BedPart.HEAD));
                set(w, fr.at(a + 6, 1, b + 6), Blocks.CRAFTING_TABLE);
                set(w, fr.at(a + 4, 4, b + 4), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
            }
    }

    private static void industrial(ServerWorld w, Frame fr, int r0, int f0) {
        int a = r0 + 3, b = f0 + 3;
        // Warehouse shell.
        walls(w, fr.at(a, 0, b), fr.at(a + 20, 8, b + 14), Blocks.GRAY_CONCRETE.getDefaultState());
        fill(w, fr.at(a, 9, b), fr.at(a + 20, 9, b + 14), Blocks.SMOOTH_STONE_SLAB);
        fill(w, fr.at(a + 8, 0, b), fr.at(a + 12, 4, b), Blocks.AIR); // big bay door
        // Smelting line.
        for (int r = a + 2; r <= a + 18; r += 2) {
            set(w, fr.at(r, 0, b + 12), Blocks.BLAST_FURNACE.getDefaultState().with(AbstractFurnaceBlock.FACING, fr.back()));
        }
        // Chimneys with smoke (campfire on top).
        for (int r : new int[]{a + 4, a + 16}) {
            fill(w, fr.at(r, 9, b + 12), fr.at(r, 15, b + 12), Blocks.BRICKS);
            set(w, fr.at(r, 16, b + 12), Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, true));
        }
        // Loading dock with barrels.
        fill(w, fr.at(a + 6, 0, b + 16), fr.at(a + 14, 0, b + 20), Blocks.SMOOTH_STONE);
        for (int r = a + 7; r <= a + 13; r += 2) set(w, fr.at(r, 1, b + 18), Blocks.BARREL);
        fillContainer(w, fr.at(a + 7, 1, b + 18), new ItemStack(Items.IRON_INGOT, 32), new ItemStack(Items.COAL, 64));
    }

    private static void constructionSite(ServerWorld w, Frame fr, int r0, int f0) {
        int a = r0 + 3, b = f0 + 3;
        fill(w, fr.at(a, -1, b), fr.at(a + 24, -1, b + 24), Blocks.COARSE_DIRT);
        // Unfinished concrete frame: columns + two partial floors.
        for (int i = 0; i <= 12; i += 6)
            for (int j = 0; j <= 12; j += 6)
                fill(w, fr.at(a + 4 + i, 0, b + 4 + j), fr.at(a + 4 + i, 10, b + 4 + j), Blocks.WHITE_CONCRETE);
        fill(w, fr.at(a + 4, 5, b + 4), fr.at(a + 16, 5, b + 16), Blocks.WHITE_CONCRETE);
        fill(w, fr.at(a + 4, 10, b + 4), fr.at(a + 10, 10, b + 16), Blocks.WHITE_CONCRETE);
        // Scaffolding stack.
        fill(w, fr.at(a + 18, 0, b + 4), fr.at(a + 18, 9, b + 4), Blocks.SCAFFOLDING);
        // Crane: mast + jib + hanging chain with hook.
        fill(w, fr.at(a + 21, 0, b + 20), fr.at(a + 21, 16, b + 20), Blocks.IRON_BLOCK);
        fill(w, fr.at(a + 12, 16, b + 20), fr.at(a + 21, 16, b + 20), Blocks.IRON_BLOCK);
        fill(w, fr.at(a + 13, 12, b + 20), fr.at(a + 13, 15, b + 20), Blocks.CHAIN);
        set(w, fr.at(a + 13, 11, b + 20), Blocks.IRON_BLOCK);
        // Material piles + supply chest.
        fill(w, fr.at(a + 1, 0, b + 20), fr.at(a + 3, 1, b + 22), Blocks.SAND);
        set(w, fr.at(a + 1, 0, b + 17), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, fr.forward()));
        fillContainer(w, fr.at(a + 1, 0, b + 17), new ItemStack(Items.WHITE_CONCRETE, 64), new ItemStack(Items.SCAFFOLDING, 64), new ItemStack(Items.IRON_BARS, 32));
    }

    private static void park(ServerWorld w, Frame fr, int r0, int f0) {
        int a = r0 + 3, b = f0 + 3;
        fill(w, fr.at(a, -1, b), fr.at(a + 24, -1, b + 24), Blocks.GRASS_BLOCK);
        // Paths.
        fill(w, fr.at(a + 12, -1, b), fr.at(a + 12, -1, b + 24), Blocks.DIRT_PATH);
        fill(w, fr.at(a, -1, b + 12), fr.at(a + 24, -1, b + 12), Blocks.DIRT_PATH);
        // Trees.
        for (int[] t : new int[][]{{4, 4}, {20, 4}, {4, 20}, {20, 20}, {8, 16}, {16, 8}}) {
            tree(w, fr, a + t[0], b + t[1]);
        }
        // Gazebo (fence posts + slab roof + benches).
        int gr = a + 18, gf = b + 18;
        for (int[] c : new int[][]{{0, 0}, {4, 0}, {0, 4}, {4, 4}}) {
            fill(w, fr.at(gr + c[0], 0, gf + c[1]), fr.at(gr + c[0], 2, gf + c[1]), Blocks.OAK_FENCE);
        }
        fill(w, fr.at(gr - 1, 3, gf - 1), fr.at(gr + 5, 3, gf + 5), Blocks.OAK_SLAB);
        set(w, fr.at(gr + 2, 0, gf + 2), Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, false));
        // Statue: pedestal + armor-stand hero.
        fill(w, fr.at(a + 12, 0, b + 12), fr.at(a + 12, 1, b + 12), Blocks.CHISELED_STONE_BRICKS);
        Vec3d sp = Vec3d.ofBottomCenter(fr.at(a + 12, 2, b + 12));
        ArmorStandEntity statue = new ArmorStandEntity(EntityType.ARMOR_STAND, w);
        statue.refreshPositionAndAngles(sp.x, sp.y, sp.z, fr.back().asRotation(), 0f);
        statue.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
        statue.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
        w.spawnEntity(statue);
        // Pond.
        fill(w, fr.at(a + 3, -1, b + 9), fr.at(a + 7, -1, b + 13), Blocks.WATER);
    }

    private static void tree(ServerWorld w, Frame fr, int r, int f) {
        fill(w, fr.at(r, 0, f), fr.at(r, 4, f), Blocks.OAK_LOG);
        fill(w, fr.at(r - 2, 3, f - 2), fr.at(r + 2, 4, f + 2), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true));
        fill(w, fr.at(r - 1, 5, f - 1), fr.at(r + 1, 5, f + 1), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true));
        set(w, fr.at(r, 6, f), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true));
    }

    private static void airport(ServerWorld w, Frame fr, int r0, int f0) {
        int a = r0 + 1, b = f0 + 1;
        // Runway with centerline stripes.
        fill(w, fr.at(a, -1, b), fr.at(a + 10, -1, b + 28), Blocks.SMOOTH_STONE);
        for (int f = b; f <= b + 28; f += 3) set(w, fr.at(a + 5, -1, f), Blocks.WHITE_CONCRETE);
        // Hangar with a big open front.
        int ha = a + 14, hb = b + 4;
        walls(w, fr.at(ha, 0, hb), fr.at(ha + 14, 7, hb + 18), Blocks.IRON_BLOCK.getDefaultState());
        fill(w, fr.at(ha, 8, hb), fr.at(ha + 14, 8, hb + 18), Blocks.SMOOTH_STONE_SLAB);
        fill(w, fr.at(ha + 2, 0, hb), fr.at(ha + 12, 5, hb), Blocks.AIR); // hangar door
        // Control tower.
        fill(w, fr.at(ha + 13, 0, hb + 20), fr.at(ha + 14, 9, hb + 21), Blocks.WHITE_CONCRETE);
        walls(w, fr.at(ha + 12, 10, hb + 19), fr.at(ha + 15, 12, hb + 22), Blocks.GLASS.getDefaultState());
        fill(w, fr.at(ha + 12, 13, hb + 19), fr.at(ha + 15, 13, hb + 22), Blocks.SMOOTH_STONE_SLAB);
        // Park a StructureCraft airplane inside the hangar.
        Vec3d sp = Vec3d.ofBottomCenter(fr.at(ha + 7, 0, hb + 9));
        var plane = ModEntities.AIRPLANE.create(w);
        if (plane != null) {
            plane.refreshPositionAndAngles(sp.x, sp.y, sp.z, fr.back().asRotation(), 0f);
            w.spawnEntity(plane);
        }
    }

    private MegaCity() {}
}
