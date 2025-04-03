package com.hbm.entity.projectile;

import api.hbm.entity.IRadarDetectable;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.items.weapon.ItemAmmoArty;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityArtilleryShell extends EntityThrowableNT implements IChunkLoader, IRadarDetectable {

    private Ticket loaderTicket; // Ticket for chunk loading

    private int turnProgress; // Progress for interpolation of position and rotation
    private double syncPosX, syncPosY, syncPosZ; // Synchronized position for smooth client-side movement
    private double syncYaw, syncPitch; // Synchronized rotation for smooth client-side movement
    @SideOnly(Side.CLIENT)
    private double velocityX, velocityY, velocityZ; // Client-side velocity for interpolation

    private double targetX, targetY, targetZ; // Target coordinates for the shell
    private boolean shouldWhistle = false; // Whether the shell should whistle
    private boolean didWhistle = false; // Whether the shell has already whistled

    private ItemStack cargo = null; // Cargo carried by the shell

    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityArtilleryShell.class, DataSerializers.VARINT); // Type of artillery shell

    public EntityArtilleryShell(World world) {
        super(world);
        this.ignoreFrustumCheck = true; // Always render the entity
        this.setSize(0.5F, 0.5F); // Set entity size
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        init(ForgeChunkManager.requestTicket(MainRegistry.instance, world, ForgeChunkManager.Type.ENTITY)); // Initialize chunk loader
        this.dataManager.register(TYPE, 0); // Register the shell type
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true; // Always render regardless of distance
    }

    public EntityArtilleryShell setType(int type) {
        this.dataManager.set(TYPE, type); // Set the shell type
        return this;
    }

    public ItemAmmoArty.ArtilleryShell getType() {
        try {
            return ItemAmmoArty.itemTypes[this.dataManager.get(TYPE)]; // Get the shell type
        } catch (Exception ex) {
            return ItemAmmoArty.itemTypes[0]; // Default to the first type if an error occurs
        }
    }

    public double[] getTarget() {
        return new double[] { this.targetX, this.targetY, this.targetZ }; // Get the target coordinates
    }

    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z; // Set the target coordinates
    }

    public double getTargetHeight() {
        return this.targetY; // Get the target's Y-coordinate
    }

    public void setWhistle(boolean whistle) {
        this.shouldWhistle = whistle; // Set whether the shell should whistle
    }

    public boolean getWhistle() {
        return this.shouldWhistle; // Get whether the shell should whistle
    }

    public boolean didWhistle() {
        return this.didWhistle; // Get whether the shell has already whistled
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            super.onUpdate();

            // Handle whistling logic
            if (!didWhistle && this.shouldWhistle) {
                double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                double deltaX = this.posX - this.targetX;
                double deltaZ = this.posZ - this.targetZ;
                double dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                if (speed * 18 > dist) {
                    world.playSound(null, this.targetX, this.targetY, this.targetZ, HBMSoundHandler.mortarWhistle, SoundCategory.BLOCKS, 15.0F, 0.9F + rand.nextFloat() * 0.2F);
                    this.didWhistle = true; // Play whistle sound when close to the target
                }
            }

            // Load neighboring chunks
            loadNeighboringChunks((int) (posX / 16), (int) (posZ / 16));
            this.getType().onUpdate(this); // Update shell type behavior

        } else {
            // Handle interpolation for smooth movement
            if (this.turnProgress > 0) {
                double interpX = this.posX + (this.syncPosX - this.posX) / this.turnProgress;
                double interpY = this.posY + (this.syncPosY - this.posY) / this.turnProgress;
                double interpZ = this.posZ + (this.syncPosZ - this.posZ) / this.turnProgress;
                double d = MathHelper.wrapDegrees(this.syncYaw - this.rotationYaw);

                this.rotationYaw += d / this.turnProgress;
                this.rotationPitch += (this.syncPitch - this.rotationPitch) / this.turnProgress;

                --this.turnProgress;
                this.setPosition(interpX, interpY, interpZ); // Interpolate position
            } else {
                this.setPosition(this.posX, this.posY, this.posZ); // Set position directly
            }

            // Spawn smoke particles if close to the synchronized position
            if (new Vec3d(this.syncPosX - this.posX, this.syncPosY - this.posY, this.syncPosZ - this.posZ).length() < 0.2) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY + 0.5, posZ, 0.0, 0.1, 0.0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.velocityX = this.motionX = p_70016_1_;
        this.velocityY = this.motionY = p_70016_3_;
        this.velocityZ = this.motionZ = p_70016_5_; // Set velocity for interpolation
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int theNumberThree) {
        this.syncPosX = x;
        this.syncPosY = y;
        this.syncPosZ = z;
        this.syncYaw = yaw;
        this.syncPitch = pitch;
        this.turnProgress = theNumberThree; // Set synchronized position and rotation
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote) {
            // Log impact details for debugging
            MainRegistry.logger.info("########################\n Artillery shell hit at " + posX + ", " + posY + ", " + posZ + "\n########################");
            MainRegistry.logger.info("########################\n Target was  at " + targetX + ", " + targetY + ", " + targetZ + "\n########################");
            MainRegistry.logger.info("########################\n Deviation " + Math.sqrt((targetX - posX) * (targetX - posX)) + ", " + Math.sqrt((targetY - posY) * (targetY - posY)) + ", " + Math.sqrt((targetZ - posZ) * (targetZ - posZ)) + "\n########################");
            MainRegistry.logger.info("########################\n Motion Values On Impact " + this.motionX + ", " + this.motionY + ", " + this.motionZ + "\n########################");


            if (mop.typeOfHit == mop.typeOfHit.ENTITY && mop.entityHit instanceof EntityArtilleryShell) return; // Prevent self-collision
            this.getType().onImpact(this, mop); // Handle impact behavior based on shell type
        }
    }

    @Override
    public void init(Ticket ticket) {
        if (!world.isRemote && ticket != null) {
            if (loaderTicket == null) {
                loaderTicket = ticket;
                loaderTicket.bindEntity(this); // Bind the ticket to this entity
                loaderTicket.getModData();
            }
            ForgeChunkManager.forceChunk(loaderTicket, new ChunkPos(chunkCoordX, chunkCoordZ)); // Force load the chunk
        }
    }

    List<ChunkPos> loadedChunks = new ArrayList<ChunkPos>(); // List of loaded chunks

    public void loadNeighboringChunks(int newChunkX, int newChunkZ) {
        if (!world.isRemote && loaderTicket != null) {
            clearChunkLoader(); // Clear previously loaded chunks

            loadedChunks.clear();
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ)); // Add the current chunk

            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.forceChunk(loaderTicket, chunk); // Force load neighboring chunks
            }
        }
    }

    public void killAndClear() {
        this.setDead(); // Mark the entity as dead
        this.clearChunkLoader(); // Clear loaded chunks
    }

    public void clearChunkLoader() {
        if (!world.isRemote && loaderTicket != null) {
            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.unforceChunk(loaderTicket, chunk); // Unforce loaded chunks
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        nbt.setInteger("type", this.dataManager.get(TYPE)); // Save shell type
        nbt.setBoolean("shouldWhistle", this.shouldWhistle); // Save whistle state
        nbt.setBoolean("didWhistle", this.didWhistle); // Save whistle completion state
        nbt.setDouble("targetX", this.targetX); // Save target coordinates
        nbt.setDouble("targetY", this.targetY);
        nbt.setDouble("targetZ", this.targetZ);

        if (this.cargo != null)
            nbt.setTag("cargo", this.cargo.writeToNBT(new NBTTagCompound())); // Save cargo
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        this.dataManager.set(TYPE, nbt.getInteger("type")); // Load shell type
        this.shouldWhistle = nbt.getBoolean("shouldWhistle"); // Load whistle state
        this.didWhistle = nbt.getBoolean("didWhistle"); // Load whistle completion state
        this.targetX = nbt.getDouble("targetX"); // Load target coordinates
        this.targetY = nbt.getDouble("targetY");
        this.targetZ = nbt.getDouble("targetZ");

        NBTTagCompound compound = nbt.getCompoundTag("cargo");
        this.setCargo(new ItemStack(compound)); // Load cargo
    }

    @Override
    protected float getAirDrag() {
        return 1.0F; // Air drag factor
    }

    @Override
    public double getGravityVelocity() {
        return 9.81 * 0.05; // Gravity effect on the shell
    }

    @Override
    protected int groundDespawn() {
        return cargo != null ? 0 : 1200; // Despawn time based on cargo presence
    }

    @Override
    public boolean canBeCollidedWith() {
        return true; // Allow collisions with the shell
    }

    public void setCargo(ItemStack stack) {
        this.cargo = stack; // Set the cargo
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (this.cargo != null) {
                player.inventory.addItemStackToInventory(this.cargo.copy()); // Give cargo to the player
                player.inventoryContainer.detectAndSendChanges();
            }
            this.setDead(); // Remove the shell
        }

        return false;
    }

    @Override
    public RadarTargetType getTargetType() {
        return RadarTargetType.ARTILLERY; // Radar target type for this entity
    }
}
