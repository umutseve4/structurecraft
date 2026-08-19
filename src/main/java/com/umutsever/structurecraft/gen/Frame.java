package com.umutsever.structurecraft.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Local coordinate frame so every structure can be authored once and rotated
 * to face the player. Axes: +forward = away from player, +right = player's right, +up = world up.
 */
public record Frame(BlockPos origin, Direction facing) {

    /** Local (right, up, forward) -> absolute world position. */
    public BlockPos at(int right, int up, int forward) {
        Direction r = facing.rotateYClockwise();
        return origin.add(
                r.getOffsetX() * right + facing.getOffsetX() * forward,
                up,
                r.getOffsetZ() * right + facing.getOffsetZ() * forward);
    }

    public Direction forward() { return facing; }
    public Direction back()    { return facing.getOpposite(); }
    public Direction right()   { return facing.rotateYClockwise(); }
    public Direction left()    { return facing.rotateYCounterclockwise(); }
}
