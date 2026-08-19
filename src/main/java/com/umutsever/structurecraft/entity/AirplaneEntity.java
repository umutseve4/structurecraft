package com.umutsever.structurecraft.entity;

import com.umutsever.structurecraft.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/**
 * Flyable airplane. Hold forward for thrust; the plane follows the rider's
 * yaw and pitch. Lift scales with forward speed; without thrust it glides
 * and slowly descends. W = throttle, look up/down = climb/dive.
 */
public class AirplaneEntity extends Entity {
    private static final double MAX_SPEED = 1.6;
    private static final double THRUST = 0.10;

    public AirplaneEntity(EntityType<? extends AirplaneEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && !this.hasPassengers()) {
            player.startRiding(this);
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isRemoved() || this.getWorld().isClient) {
            return true;
        }
        this.removeAllPassengers();
        this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), getX(), getY(), getZ(), new ItemStack(ModItems.AIRPLANE)));
        this.discard();
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isLogicalSideForUpdatingMovement()) {
            Vec3d velocity = this.getVelocity();

            if (this.getControllingPassenger() instanceof PlayerEntity rider) {
                this.setYaw(rider.getYaw());
                this.setPitch(MathHelper.clamp(rider.getPitch(), -60f, 60f));

                if (rider.forwardSpeed > 0) {
                    // Thrust along the full look vector (yaw + pitch).
                    float yawRad = this.getYaw() * MathHelper.RADIANS_PER_DEGREE;
                    float pitchRad = this.getPitch() * MathHelper.RADIANS_PER_DEGREE;
                    Vec3d dir = new Vec3d(
                            -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad),
                            -MathHelper.sin(pitchRad),
                            MathHelper.cos(yawRad) * MathHelper.cos(pitchRad));
                    velocity = velocity.add(dir.multiply(THRUST));
                } else if (rider.forwardSpeed < 0) {
                    velocity = velocity.multiply(0.9, 1.0, 0.9); // brake
                }
            }

            // Aerodynamics: drag, lift from speed, gravity.
            double speed = velocity.horizontalLength();
            double lift = Math.min(0.08, speed * 0.06);
            velocity = velocity.multiply(0.985, 0.98, 0.985);
            if (!this.hasNoGravity()) {
                velocity = velocity.add(0, -0.08 + lift, 0);
            }
            if (velocity.length() > MAX_SPEED) {
                velocity = velocity.normalize().multiply(MAX_SPEED);
            }
            this.setVelocity(velocity);
            this.move(MovementType.SELF, this.getVelocity());
        }

        this.checkBlockCollision();
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.9;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
