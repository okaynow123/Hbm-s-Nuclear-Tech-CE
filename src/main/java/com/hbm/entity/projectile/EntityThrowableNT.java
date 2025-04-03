package com.hbm.entity.projectile;

import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.util.TrackerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


// Imports necessary classes and libraries for the entity's functionality

/**
 * Near-identical copy of EntityThrowable but deobfuscated & untangled
 * Represents a throwable entity with custom behavior
 * @author Bailie Byrne
 * 
 */
public abstract class EntityThrowableNT extends Entity implements IProjectile {
    // Coordinates of the block where the entity is stuck
    private int stuckBlockX = -1;
    private int stuckBlockY = -1;
    private int stuckBlockZ = -1;
    private Block stuckBlock; // Block where the entity is stuck
    protected boolean inGround; // Whether the entity is in the ground
    public int throwableShake; // Shake animation when thrown
    protected EntityLivingBase thrower; // The entity that threw this projectile
    private String throwerName; // Name of the thrower (used for persistence)
    private int ticksInGround; // Ticks the entity has been in the ground
    private int ticksInAir; // Ticks the entity has been in the air
    private static final DataParameter<Byte> STUCK_IN = EntityDataManager.createKey(EntityThrowableNT.class, DataSerializers.BYTE); // Tracks the side the entity is stuck in

    // Constructor for initializing the entity in the world
    public EntityThrowableNT(World worldIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F); // Set the size of the entity
    }

    // Constructor for initializing the entity at a specific position
    public EntityThrowableNT(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.ticksInGround = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z); // Set the position of the entity
    }

    // Initializes data parameters for the entity
    protected void entityInit() {
        this.dataManager.register(STUCK_IN, Byte.valueOf((byte) 0)); // Register the stuck side
    }

    // Sets the side the entity is stuck in
    public void setStuckIn(int side) {
        this.dataManager.set(STUCK_IN, (byte) side);
    }

    // Gets the side the entity is stuck in
    public int getStuckIn() {
        return this.dataManager.get(STUCK_IN);
    }

    // Returns the force of the throw
    protected float throwForce() {
        return 1.5F;
    }

    // Multiplier for heading force
    protected double headingForceMult() {
        return 0.0075D;
    }

    // Angle of the throw
    protected float throwAngle() {
        return 0.0F;
    }

    // Multiplier for motion
    protected double motionMult() {
        return 1.0D;
    }

    // Determines if the entity is in range to render
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D; // Adjust render distance
        return distance < d0 * d0;
    }

    // Constructor for initializing the entity with a thrower
    public EntityThrowableNT(World world, EntityLivingBase thrower) {
        super(world);
        this.thrower = thrower; // Set the thrower
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(thrower.posX, thrower.posY + (double) thrower.getEyeHeight(), thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
        // Adjust position to start from the thrower's eye level
        this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.posY -= 0.1D;
        this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        float velocity = 0.4F; // Initial velocity
        this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * velocity);
        this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * velocity);
        this.motionY = (double) (-MathHelper.sin((this.rotationPitch + this.throwAngle()) / 180.0F * (float) Math.PI) * velocity);
        this.shoot(this.motionX, this.motionY, this.motionZ, this.throwForce(), 1.0F); // Shoot the entity
    }

    // Shoots the entity with specified motion, velocity, and inaccuracy
    @Override
    public void shoot(double motionX, double motionY, double motionZ, float velocity, float inaccuracy) {
        float throwLen = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ); // Calculate Euclidean distance

        motionX /= (double) throwLen; // Normalize motion
        motionY /= (double) throwLen;
        motionZ /= (double) throwLen;

        motionX *= (double) velocity; // Apply velocity
        motionY *= (double) velocity;
        motionZ *= (double) velocity;

        this.motionX = motionX; // Set motion
        this.motionY = motionY;
        this.motionZ = motionZ;

        float hyp = MathHelper.sqrt(motionX * motionX + motionZ * motionZ); // Hypotenuse for rotation calculations

        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI); // Set yaw
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(motionY, (double) hyp) * 180.0D / Math.PI); // Set pitch

        this.ticksInGround = 0; // Reset ticks in ground
    }

    // Sets the velocity of the entity
    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float hyp = MathHelper.sqrt(x * x + z * z); // Hypotenuse for rotation calculations
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI); // Set yaw
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(y, (double) hyp) * 180.0D / Math.PI); // Set pitch
        }
    }

    // Main update method for the entity
    @Override
    public void onUpdate() {
        super.onUpdate(); // Call parent update logic

        if (this.throwableShake > 0) {
            --this.throwableShake; // Decrease shake animation
        }

        if (this.inGround) {
            // Logic for when the entity is stuck in the ground
            if (this.world.getBlockState(new BlockPos(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ)).getBlock() == this.stuckBlock) {
                ++this.ticksInGround; // Increment ticks in ground

                if (this.groundDespawn() > 0 && this.ticksInGround == this.groundDespawn()) {
                    this.setDead(); // Despawn the entity after a certain time
                }

                return;
            } else {
                this.inGround = false; // Entity is no longer in the ground
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir; // Increment ticks in air

            // Calculate current and next positions for ray tracing
            Vec3 pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 nextPos = Vec3.createVectorHelper(this.posX + this.motionX * motionMult(), this.posY + this.motionY * motionMult(), this.posZ + this.motionZ * motionMult());
            RayTraceResult mop = null;

            if (!this.isSpectral()) mop = this.world.rayTraceBlocks(pos.toVec3d(), nextPos.toVec3d(), false, true, false); // Perform ray tracing

            pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            nextPos = Vec3.createVectorHelper(this.posX + this.motionX * motionMult(), this.posY + this.motionY * motionMult(), this.posZ + this.motionZ * motionMult());

            if (mop != null) {
                nextPos = Vec3.createVectorHelper(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z); // Adjust next position if a block is hit
            }

            if (!this.world.isRemote && this.doesImpactEntities()) {
                // Check for entity collisions
                Entity hitEntity = null;
                List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX * motionMult(), this.motionY * motionMult(), this.motionZ * motionMult()).expand(1.0D, 1.0D, 1.0D));
                double nearest = 0.0D;
                EntityLivingBase thrower = this.getThrower();
                RayTraceResult nonPenImpact = null;

                for (int j = 0; j < list.size(); ++j) {
                    Entity entity = (Entity) list.get(j);

                    // Exclude the thrower and check for collisions
                    if (entity.canBeCollidedWith() && (entity != thrower || this.ticksInAir >= this.selfDamageDelay())) {
                        double hitbox = 0.3F;
                        AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(hitbox, hitbox, hitbox);

                        RayTraceResult hitMop = aabb.calculateIntercept(pos.toVec3d(), nextPos.toVec3d()); // Calculate intersection

                        if (hitMop != null) {
                            if (this.doesPenetrate()) {
                                this.onImpact(new RayTraceResult(entity, hitMop.hitVec)); // Handle penetration
                            } else {
                                Vec3 hitVec = Vec3.createVectorHelper(hitMop.hitVec.x, hitMop.hitVec.y, hitMop.hitVec.z);
                                double dist = pos.distanceTo(hitVec);

                                if (dist < nearest || nearest == 0.0D) {
                                    hitEntity = entity;
                                    nearest = dist;
                                    nonPenImpact = hitMop;
                                }
                            }
                        }
                    }
                }

                if (!this.doesPenetrate() && hitEntity != null) {
                    mop = new RayTraceResult(hitEntity, nonPenImpact.hitVec); // Handle non-penetration impact
                }
            }

            if (mop != null) {
                if (mop.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(mop.getBlockPos()) == Blocks.PORTAL) {
                    this.setPortal(mop.getBlockPos()); // Handle portal interaction
                } else {
                    this.onImpact(mop); // Handle impact
                }
            }

            // Update motion and rotation during flight
            if (!this.onGround) {
                float hyp = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

                for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) hyp) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F);

                while (this.rotationPitch - this.prevRotationPitch >= 180.0F) this.prevRotationPitch += 360.0F;
                while (this.rotationYaw - this.prevRotationYaw < -180.0F) this.prevRotationYaw -= 360.0F;
                while (this.rotationYaw - this.prevRotationYaw >= 180.0F) this.prevRotationYaw += 360.0F;

                this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
                this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            }

            float drag = this.getAirDrag(); // Air drag
            double gravity = this.getGravityVelocity(); // Gravity Not sure why this is calculated every update, but it is

            this.posX += this.motionX * motionMult(); // Update position
            this.posY += this.motionY * motionMult();
            this.posZ += this.motionZ * motionMult();
            

            if (this.isInWater()) {
                // Handle water interaction
                for (int i = 0; i < 4; ++i) {
                    float f = 0.25F;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) f, this.posY - this.motionY * (double) f, this.posZ - this.motionZ * (double) f, this.motionX, this.motionY, this.motionZ);
                }

                drag = this.getWaterDrag(); // Water drag
            }

            this.motionX *= (double) drag; // Apply drag
            this.motionY *= (double) drag;
            this.motionZ *= (double) drag;
            this.motionY -= gravity; // Apply gravity
            this.setPosition(this.posX, this.posY, this.posZ); // Update position
        }
    }

    // Determines if the entity impacts other entities
    public boolean doesImpactEntities() {
        return true;
    }

    // Determines if the entity penetrates through entities
    public boolean doesPenetrate() {
        return false;
    }

    // Determines if the entity is spectral
    public boolean isSpectral() {
        return false;
    }

    // Delay before the thrower can be damaged by the entity
    public int selfDamageDelay() {
        return 5;
    }

    // Handles the entity getting stuck in a block
    public void getStuck(BlockPos pos, int side) {
        this.stuckBlockX = pos.getX();
        this.stuckBlockY = pos.getY();
        this.stuckBlockZ = pos.getZ();
        this.stuckBlock = world.getBlockState(pos).getBlock();
        this.inGround = true;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.setStuckIn(side);
        TrackerUtil.sendTeleport(world, this); // Notify tracker
    }

    // Returns the gravity velocity
    public double getGravityVelocity() {
        return 0.03D;
    }

    // Abstract method for handling impact logic
    protected abstract void onImpact(RayTraceResult result);

    // Writes entity data to NBT
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("xTile", this.stuckBlockX);
        compound.setInteger("yTile", this.stuckBlockY);
        compound.setInteger("zTile", this.stuckBlockZ);
        compound.setByte("inTile", (byte) Block.getIdFromBlock(this.stuckBlock));
        compound.setByte("shake", (byte) this.throwableShake);
        compound.setByte("inGround", (byte) (this.inGround ? 1 : 0));

        if ((this.throwerName == null || this.throwerName.isEmpty()) && this.thrower instanceof EntityPlayer) {
            this.throwerName = this.thrower.getCommandSenderEntity().getName();
        }

        compound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
    }

    // Reads entity data from NBT
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.stuckBlockX = compound.getShort("xTile");
        this.stuckBlockY = compound.getShort("yTile");
        this.stuckBlockZ = compound.getShort("zTile");
        this.stuckBlock = Block.getBlockById(compound.getByte("inTile") & 255);
        this.throwableShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;
        this.throwerName = compound.getString("ownerName");

        if (this.throwerName != null && this.throwerName.isEmpty()) this.throwerName = null;
    }

    // Sets the thrower of the entity
    public void setThrower(EntityLivingBase thrower) {
        this.thrower = thrower;
    }

    // Gets the thrower of the entity
    public EntityLivingBase getThrower() {
        if (this.thrower == null && this.throwerName != null && this.throwerName.length() > 0) {
            this.thrower = this.world.getPlayerEntityByName(this.throwerName);
        }
        return this.thrower;
    }

    /* ================================== Additional Getters =====================================*/

    // Returns the air drag
    protected float getAirDrag() {
        return 0.99F;
    }

    // Returns the water drag
    protected float getWaterDrag() {
        return 0.8F;
    }

    // Returns the time before the entity despawns when stuck in the ground
    protected int groundDespawn() {
        return 1200;
    }
}
