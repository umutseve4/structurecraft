package com.umutsever.structurecraft.entity;

import com.umutsever.structurecraft.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
 * Rideable skateboard. Steering follows the rider's look direction, forward key
 * accelerates. Leaving the ground at speed triggers a 360 "trick" spin that the
 * renderer animates (synced via DataTracker).
 */
public class SkateboardEntity extends Entity {
    private static final TrackedData<Integer> TRICK_TICKS =
            DataTracker.registerData(SkateboardEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final int TRICK_LENGTH = 12;

    private boolean wasOnGround = true;

    public SkateboardEntity(EntityType<? extends SkateboardEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(TRICK_TICKS, 0);
    }

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
        this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), getX(), getY(), getZ(), new ItemStack(ModItems.SKATEBOARD)));
        this.discard();
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        int trick = getTrickTicks();
        if (trick > 0) {
            this.dataTracker.set(TRICK_TICKS, trick - 1);
        }

        if (this.isLogicalSideForUpdatingMovement()) {
            Vec3d velocity = this.getVelocity();

            if (this.getControllingPassenger() instanceof PlayerEntity rider) {
                this.setYaw(rider.getYaw());
                float forwardInput = rider.forwardSpeed;
                if (forwardInput != 0 && this.isOnGround()) {
                    float yawRad = this.getYaw() * MathHelper.RADIANS_PER_DEGREE;
                    Vec3d dir = new Vec3d(-MathHelper.sin(yawRad), 0, MathHelper.cos(yawRad));
                    velocity = velocity.add(dir.multiply(forwardInput > 0 ? 0.08 : -0.04));
                }
                // Trick trigger: airborne with speed.
                if (wasOnGround && !this.isOnGround() && velocity.horizontalLengthSquared() > 0.05) {
                    this.dataTracker.set(TRICK_TICKS, TRICK_LENGTH);
                }
            }

            // Friction + gravity + speed cap.
            velocity = velocity.multiply(this.isOnGround() ? 0.93 : 0.99, 1.0, this.isOnGround() ? 0.93 : 0.99);
            if (!this.hasNoGravity()) {
                velocity = velocity.add(0, -0.06, 0);
            }
            double cap = 0.7;
            double h = velocity.horizontalLength();
            if (h > cap) {
                velocity = new Vec3d(velocity.x / h * cap, velocity.y, velocity.z / h * cap);
            }
            this.setVelocity(velocity);
            this.move(MovementType.SELF, this.getVelocity());
        }

        wasOnGround = this.isOnGround();
        this.checkBlockCollision();
    }

    public int getTrickTicks() {
        return this.dataTracker.get(TRICK_TICKS);
    }

    /** 0..1 animation progress for the renderer. */
    public float getTrickProgress(float tickDelta) {
        int t = getTrickTicks();
        if (t <= 0) return 0f;
        return MathHelper.clamp((TRICK_LENGTH - t + tickDelta) / (float) TRICK_LENGTH, 0f, 1f);
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.35;
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
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
