package com.umutsever.structurecraft.gen;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static com.umutsever.structurecraft.gen.BuildHelper.*;

/**
 * Multi-story fortress: moat + iron gate, courtyard fountain, corner towers with ladders,
 * keep with dining hall, storage, library, nether portal room, living quarters,
 * villager pen and a rooftop beacon room.
 */
public final class LateGameFortress {

    public static void generate(ServerWorld w, BlockPos origin, Direction facing) {
        Frame fr = new Frame(origin, facing);

        // ---- Site prep ----
        fill(w, fr.at(-26, 0, -2), fr.at(26, 40, 52), Blocks.AIR);
        fill(w, fr.at(-26, -1, -2), fr.at(26, -1, 52), Blocks.STONE);

        moatAndBridge(w, fr);
        curtainWalls(w, fr);
        gate(w, fr);
        towers(w, fr);
        courtyardAndFountain(w, fr);
        keep(w, fr);
        beaconRoom(w, fr);
    }

    private static void moatAndBridge(ServerWorld w, Frame fr) {
        // Water moat ring (3 wide, 2 deep) just outside the walls.
        BlockState water = Blocks.WATER.getDefaultState();
        for (int ring = 0; ring < 3; ring++) {
            int o = 20 + ring;
            for (int r = -o; r <= o; r++)
                for (int f = 4 - ring; f <= 44 + ring; f++)
                    if (Math.abs(r) >= 20 || f <= 6 || f >= 42) {
                        if (Math.abs(r) == o || f == 4 - ring || f == 44 + ring || Math.abs(r) >= 20 && (f < 7 || f > 41)) {
                            fill(w, fr.at(r, -2, f), fr.at(r, -1, f), water);
                        }
                    }
        }
        // Explicit simple moat: hollow rectangle band between radius 20..22.
        for (int r = -23; r <= 23; r++) {
            for (int f = 1; f <= 47; f++) {
                boolean inOuter = Math.abs(r) <= 23 && f >= 1 && f <= 47;
                boolean inInner = Math.abs(r) <= 19 && f >= 5 && f <= 43;
                if (inOuter && !inInner) {
                    fill(w, fr.at(r, -2, f), fr.at(r, -1, f), water);
                }
            }
        }
        // Stone bridge across the moat to the gate.
        fill(w, fr.at(-2, -1, 0), fr.at(2, -1, 6), Blocks.STONE_BRICKS);
        fill(w, fr.at(-2, 0, 0), fr.at(2, 0, 6), Blocks.AIR);
    }

    private static void curtainWalls(ServerWorld w, Frame fr) {
        BlockState brick = Blocks.STONE_BRICKS.getDefaultState();
        walls(w, fr.at(-18, 0, 6), fr.at(18, 11, 42), brick);
        // Battlements (crenellation) on top.
        for (int r = -18; r <= 18; r += 2) {
            set(w, fr.at(r, 12, 6), brick);
            set(w, fr.at(r, 12, 42), brick);
        }
        for (int f = 6; f <= 42; f += 2) {
            set(w, fr.at(-18, 12, f), brick);
            set(w, fr.at(18, 12, f), brick);
        }
        // Wall walk torches.
        for (int r = -16; r <= 16; r += 4) {
            set(w, fr.at(r, 12, 7), Blocks.TORCH);
            set(w, fr.at(r, 12, 41), Blocks.TORCH);
        }
    }

    private static void gate(ServerWorld w, Frame fr) {
        // Grand iron gate: 4-wide opening, iron bars above, iron doors below.
        fill(w, fr.at(-2, 0, 6), fr.at(2, 4, 6), Blocks.AIR);
        fill(w, fr.at(-2, 3, 6), fr.at(2, 4, 6), Blocks.IRON_BARS);
        BlockState lower = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, fr.forward()).with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        for (int r = -1; r <= 1; r += 2) {
            set(w, fr.at(r, 0, 6), lower);
            set(w, fr.at(r, 1, 6), lower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        }
        set(w, fr.at(0, 0, 6), Blocks.AIR);
        fill(w, fr.at(0, 1, 6), fr.at(0, 4, 6), Blocks.IRON_BARS);
        // Buttons to open the doors from both sides.
        set(w, fr.at(-3, 1, 5), Blocks.STONE_BUTTON.getDefaultState().with(WallMountedBlock.FACING, fr.back()));
        set(w, fr.at(-3, 1, 7), Blocks.STONE_BUTTON.getDefaultState().with(WallMountedBlock.FACING, fr.forward()));
        // Gatehouse decor.
        fill(w, fr.at(-4, 0, 6), fr.at(-3, 6, 6), Blocks.POLISHED_ANDESITE);
        fill(w, fr.at(3, 0, 6), fr.at(4, 6, 6), Blocks.POLISHED_ANDESITE);
        fill(w, fr.at(-4, 5, 6), fr.at(4, 6, 6), Blocks.POLISHED_ANDESITE);
    }

    private static void towers(ServerWorld w, Frame fr) {
        int[][] corners = {{-18, 6}, {18, 6}, {-18, 42}, {18, 42}};
        BlockState brick = Blocks.STONE_BRICKS.getDefaultState();
        for (int[] c : corners) {
            int cr = c[0], cf = c[1];
            // 5x5 tower shell up to y17.
            walls(w, fr.at(cr - 2, 0, cf - 2), fr.at(cr + 2, 17, cf + 2), brick);
            fill(w, fr.at(cr - 2, 17, cf - 2), fr.at(cr + 2, 17, cf + 2), brick);
            fill(w, fr.at(cr - 1, 17, cf - 1), fr.at(cr + 1, 17, cf + 1), Blocks.AIR);
            fill(w, fr.at(cr - 1, 16, cf - 1), fr.at(cr + 1, 16, cf + 1), brick); // lookout floor
            // Climbable ladder shaft (interior center, attached to inner north wall of the tower).
            Direction ladderFace = fr.back();
            BlockState ladder = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, ladderFace);
            for (int y = 0; y <= 16; y++) set(w, fr.at(cr, y, cf), ladder.equals(ladder) ? Blocks.AIR.getDefaultState() : ladder);
            for (int y = 0; y <= 16; y++) set(w, fr.at(cr, y, cf - 1 == cf ? cf : cf), Blocks.AIR.getDefaultState());
            for (int y = 0; y <= 16; y++) {
                set(w, fr.at(cr, y, cf), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.forward()));
            }
            set(w, fr.at(cr, 17, cf), Blocks.AIR);
            // Ground doorway into the tower from the courtyard side.
            int dr = cr > 0 ? cr - 2 : cr + 2;
            fill(w, fr.at(dr, 0, cf), fr.at(dr, 1, cf), Blocks.AIR);
            // Spire.
            for (int i = 0; i < 3; i++) {
                walls(w, fr.at(cr - 2 + i, 18 + i, cf - 2 + i), fr.at(cr + 2 - i, 18 + i, cf + 2 - i), brick);
            }
            set(w, fr.at(cr, 21, cf), Blocks.STONE_BRICK_WALL);
            set(w, fr.at(cr, 22, cf), Blocks.TORCH);
        }
    }

    private static void courtyardAndFountain(ServerWorld w, Frame fr) {
        fill(w, fr.at(-17, -1, 7), fr.at(17, -1, 41), Blocks.POLISHED_ANDESITE);
        // Central fountain in the front courtyard.
        int cf = 12;
        fill(w, fr.at(-3, 0, cf - 3), fr.at(3, 0, cf + 3), Blocks.QUARTZ_BLOCK);
        fill(w, fr.at(-2, 0, cf - 2), fr.at(2, 0, cf + 2), Blocks.WATER);
        fill(w, fr.at(0, 0, cf), fr.at(0, 3, cf), Blocks.QUARTZ_PILLAR);
        set(w, fr.at(0, 4, cf), Blocks.SEA_LANTERN);
        set(w, fr.at(0, 5, cf), Blocks.WATER);
    }

    private static void keep(ServerWorld w, Frame fr) {
        BlockState brick = Blocks.STONE_BRICKS.getDefaultState();
        BlockState planks = Blocks.DARK_OAK_PLANKS.getDefaultState();
        // Keep shell: r[-14..14], f[18..40], 3 floors of height 5 (y0..15) + roof slab at y16.
        fill(w, fr.at(-14, -1, 18), fr.at(14, -1, 40), planks);
        walls(w, fr.at(-14, 0, 18), fr.at(14, 15, 40), brick);
        fill(w, fr.at(-14, 16, 18), fr.at(14, 16, 40), brick);
        // Intermediate floors.
        fill(w, fr.at(-13, 5, 19), fr.at(13, 5, 39), planks);
        fill(w, fr.at(-13, 10, 19), fr.at(13, 10, 39), planks);
        // Grand entrance (double doors).
        fill(w, fr.at(-1, 0, 18), fr.at(1, 2, 18), Blocks.AIR);
        BlockState lower = Blocks.DARK_OAK_DOOR.getDefaultState().with(DoorBlock.FACING, fr.back()).with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        set(w, fr.at(-1, 0, 18), lower); set(w, fr.at(-1, 1, 18), lower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        set(w, fr.at(1, 0, 18), lower);  set(w, fr.at(1, 1, 18), lower.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        set(w, fr.at(0, 0, 18), brick);  set(w, fr.at(0, 1, 18), Blocks.GLASS_PANE); set(w, fr.at(0, 2, 18), brick);
        // Windows on every floor.
        for (int y : new int[]{2, 7, 12}) {
            for (int f = 21; f <= 37; f += 4) {
                set(w, fr.at(-14, y, f), Blocks.GLASS_PANE);
                set(w, fr.at(14, y, f), Blocks.GLASS_PANE);
            }
        }
        // Interior staircase column (ladders) connecting all floors, back-center.
        for (int y = 0; y <= 15; y++) {
            set(w, fr.at(0, y, 39), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.back()));
        }
        fill(w, fr.at(0, 5, 39), fr.at(0, 5, 39), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.back()));
        set(w, fr.at(0, 5, 38), Blocks.AIR); set(w, fr.at(0, 10, 38), Blocks.AIR); // hatch openings
        fill(w, fr.at(0, 5, 39), fr.at(0, 5, 39), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.back()));
        set(w, fr.at(0, 5, 39), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.back()));
        set(w, fr.at(0, 10, 39), Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, fr.back()));

        diningHall(w, fr);
        portalRoom(w, fr);
        villagerPen(w, fr);
        storageRoom(w, fr);
        library(w, fr);
        livingQuarters(w, fr);
    }

    // ---- Floor 1 (y0..4) ----
    private static void diningHall(ServerWorld w, Frame fr) {
        // Long banquet table down the center.
        for (int f = 24; f <= 34; f++) {
            set(w, fr.at(0, 0, f), Blocks.DARK_OAK_FENCE);
            set(w, fr.at(0, 1, f), Blocks.DARK_OAK_SLAB);
            if (f % 2 == 0) {
                set(w, fr.at(-1, 0, f), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, fr.right()));
                set(w, fr.at(1, 0, f), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, fr.left()));
            }
        }
        set(w, fr.at(0, 2, 26), Blocks.CAKE);
        // Chandeliers: chain + hanging lantern rows.
        for (int f = 24; f <= 34; f += 5) {
            set(w, fr.at(0, 4, f), Blocks.CHAIN);
            set(w, fr.at(0, 3, f), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
            for (int r : new int[]{-4, 4}) {
                set(w, fr.at(r, 4, f), Blocks.CHAIN);
                set(w, fr.at(r, 3, f), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
            }
        }
        // Red carpet aisle.
        fill(w, fr.at(-2, 0, 19), fr.at(2, 0, 23), Blocks.RED_CARPET);
    }

    private static void portalRoom(ServerWorld w, Frame fr) {
        // Left wing, floor 1: nether portal + nether wart farm.
        fill(w, fr.at(-13, 0, 19), fr.at(-6, 4, 27), Blocks.AIR);
        walls(w, fr.at(-6, 0, 19), fr.at(-6, 4, 27), Blocks.STONE_BRICKS.getDefaultState());
        fill(w, fr.at(-6, 1, 22), fr.at(-6, 2, 24), Blocks.AIR); // doorway
        // Obsidian frame (4x5) against the outer wall, portal axis along the frame.
        Direction.Axis axis = fr.right().getAxis();
        fill(w, fr.at(-13, 0, 21), fr.at(-13, 4, 25), Blocks.OBSIDIAN);
        fill(w, fr.at(-13, 1, 22), fr.at(-13, 3, 24), Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis));
        // Nether wart farm.
        for (int r = -11; r <= -8; r++)
            for (int f = 19; f <= 20; f++) {
                set(w, fr.at(r, -1, f), Blocks.SOUL_SAND);
                set(w, fr.at(r, 0, f), Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3));
            }
        set(w, fr.at(-9, 4, 23), Blocks.GLOWSTONE);
    }

    private static void villagerPen(ServerWorld w, Frame fr) {
        // Right wing, floor 1: fenced villager holding area with job sites and beds.
        walls(w, fr.at(6, 0, 19), fr.at(13, 2, 27), Blocks.DARK_OAK_FENCE.getDefaultState());
        set(w, fr.at(6, 0, 23), Blocks.DARK_OAK_FENCE_GATE.getDefaultState().with(FenceGateBlock.FACING, fr.left()));
        set(w, fr.at(8, 0, 21), Blocks.COMPOSTER);
        set(w, fr.at(8, 0, 25), Blocks.LECTERN);
        BlockState foot = Blocks.WHITE_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedPart.FOOT == BedPart.FOOT ? BedBlock.PART : BedBlock.PART, BedPart.FOOT);
        set(w, fr.at(11, 0, 21), foot);
        set(w, fr.at(11, 0, 22), foot.with(BedBlock.PART, BedPart.HEAD));
        set(w, fr.at(12, 0, 21), foot);
        set(w, fr.at(12, 0, 22), foot.with(BedBlock.PART, BedPart.HEAD));
        for (int i = 0; i < 2; i++) {
            Vec3d sp = Vec3d.ofBottomCenter(fr.at(9 + i, 0, 23));
            VillagerEntity villager = new VillagerEntity(EntityType.VILLAGER, w);
            villager.refreshPositionAndAngles(sp.x, sp.y, sp.z, 0f, 0f);
            w.spawnEntity(villager);
        }
        set(w, fr.at(9, 4, 23), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
    }

    // ---- Floor 2 (y6..9) ----
    private static void storageRoom(ServerWorld w, Frame fr) {
        // Left half of floor 2: rows of stocked double chests.
        ItemStack[][] loot = {
                {new ItemStack(Items.IRON_INGOT, 64), new ItemStack(Items.GOLD_INGOT, 32), new ItemStack(Items.DIAMOND, 16)},
                {new ItemStack(Items.COOKED_BEEF, 64), new ItemStack(Items.GOLDEN_CARROT, 64), new ItemStack(Items.BREAD, 64)},
                {new ItemStack(Items.ARROW, 64), new ItemStack(Items.ENDER_PEARL, 16), new ItemStack(Items.BLAZE_ROD, 12)},
                {new ItemStack(Items.OBSIDIAN, 32), new ItemStack(Items.STONE_BRICKS, 64), new ItemStack(Items.OAK_PLANKS, 64)}
        };
        int i = 0;
        for (int f = 21; f <= 33; f += 4) {
            for (int r = -12; r <= -6; r += 3) {
                BlockPos p = fr.at(r, 6, f);
                set(w, p, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, fr.back()));
                fillContainer(w, p, loot[i % loot.length]);
                i++;
            }
        }
        for (int f = 22; f <= 32; f += 5) set(w, fr.at(-9, 9, f), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
    }

    private static void library(ServerWorld w, Frame fr) {
        // Right half of floor 2: enchanting library.
        for (int f = 21; f <= 37; f += 2) {
            fill(w, fr.at(13, 6, f), fr.at(13, 8, f), Blocks.BOOKSHELF);
            fill(w, fr.at(6, 6, f), fr.at(6, 8, f), Blocks.BOOKSHELF);
        }
        set(w, fr.at(9, 6, 28), Blocks.ENCHANTING_TABLE);
        // Bookshelf ring around the enchanting table (full power).
        for (int r = 7; r <= 11; r++)
            for (int f = 26; f <= 30; f++)
                if (r == 7 || r == 11 || f == 26 || f == 30) {
                    if (!(r == 9 && f == 26)) set(w, fr.at(r, 6, f), Blocks.BOOKSHELF);
                }
        set(w, fr.at(9, 6, 26), Blocks.RED_CARPET); // entrance gap
        set(w, fr.at(10, 6, 32), Blocks.LECTERN);
        set(w, fr.at(9, 9, 28), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
    }

    // ---- Floor 3 (y11..14) ----
    private static void livingQuarters(ServerWorld w, Frame fr) {
        for (int i = 0; i < 4; i++) {
            int r = -10 + i * 6;
            BlockState foot = Blocks.BLUE_BED.getDefaultState().with(BedBlock.FACING, fr.forward()).with(BedBlock.PART, BedPart.FOOT);
            set(w, fr.at(r, 11, 21), foot);
            set(w, fr.at(r, 11, 22), foot.with(BedBlock.PART, BedPart.HEAD));
            set(w, fr.at(r + 1, 11, 21), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, fr.back()));
            set(w, fr.at(r, 11, 23), Blocks.BLUE_CARPET);
        }
        // Lounge.
        fill(w, fr.at(-3, 11, 32), fr.at(3, 11, 35), Blocks.BLUE_CARPET);
        set(w, fr.at(0, 11, 33), Blocks.JUKEBOX);
        for (int r = -8; r <= 8; r += 8) set(w, fr.at(r, 14, 28), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));
    }

    private static void beaconRoom(ServerWorld w, Frame fr) {
        // Rooftop beacon room on the keep (glass enclosure, iron base, active beacon).
        walls(w, fr.at(-4, 17, 25), fr.at(4, 21, 33), Blocks.GLASS.getDefaultState());
        fill(w, fr.at(-4, 22, 25), fr.at(4, 22, 33), Blocks.GLASS);
        fill(w, fr.at(-1, 17, 28), fr.at(1, 17, 30), Blocks.IRON_BLOCK);
        set(w, fr.at(0, 18, 29), Blocks.BEACON);
        set(w, fr.at(0, 22, 29), Blocks.GLASS); // beam passes through glass
        fill(w, fr.at(-4, 17, 28), fr.at(-4, 18, 30), Blocks.AIR); // entrance from roof walk
        // Roof walk access hole above the keep ladder.
        set(w, fr.at(0, 16, 39), Blocks.AIR);
        set(w, fr.at(0, 16, 38), Blocks.AIR);
    }

    private LateGameFortress() {}
}
