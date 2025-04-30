package com.hbm.entity.projectile;

import api.hbm.entity.IRadarDetectable;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.entity.projectile.rocketbehavior.IRocketSteeringBehavior;
import com.hbm.entity.projectile.rocketbehavior.IRocketTargetingBehavior;
import com.hbm.entity.projectile.rocketbehavior.RocketSteeringBallisticArc;
import com.hbm.entity.projectile.rocketbehavior.RocketTargetingPredictive;
import com.hbm.items.weapon.ItemAmmoHIMARS;
import com.hbm.main.MainRegistry;
import com.hbm.render.amlfrom1710.Vec3;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityArtilleryRocket extends EntityThrowableInterp
        implements IChunkLoader, IRadarDetectable {

    private Ticket loaderTicket;

    public Entity targetEntity = null;
    public Vec3 lastTargetPos;

    public IRocketTargetingBehavior targeting;
    public IRocketSteeringBehavior steering;

    private static final DataParameter<Integer> TYPE =
            EntityDataManager.createKey(EntityArtilleryRocket.class, DataSerializers.VARINT);

    public EntityArtilleryRocket(World world) {
        super(world);
        this.ignoreFrustumCheck = true;

        this.targeting = new RocketTargetingPredictive();
        this.steering = new RocketSteeringBallisticArc();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        init(
                ForgeChunkManager.requestTicket(
                        MainRegistry.instance, world, ForgeChunkManager.Type.ENTITY));
        this.dataManager.register(TYPE, 10);
    }

    public ItemAmmoHIMARS.HIMARSRocket getRocket() {
        try {
            return ItemAmmoHIMARS.itemTypes[this.dataManager.get(TYPE)];
        } catch (Exception ex) {
            return ItemAmmoHIMARS.itemTypes[0];
        }
    }

    public EntityArtilleryRocket setTarget(Entity target) {
        this.targetEntity = target;
        setTarget(target.posX, target.posY - target.getYOffset() + target.height / 2D, target.posZ);
        return this;
    }

    public EntityArtilleryRocket setTarget(double x, double y, double z) {
        this.lastTargetPos = Vec3.createVectorHelper(x, y, z);
        return this;
    }

    public Vec3 getLastTarget() {
        return this.lastTargetPos;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    public EntityArtilleryRocket setType(int type) {
        this.dataManager.set(TYPE, type);
        return this;
    }

    @Override
    protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote) {
            this.getRocket().onImpact(this, mop);
        }
    }

    @Override
    public void init(ForgeChunkManager.Ticket ticket) {
        if (!world.isRemote && ticket != null) {
            if (loaderTicket == null) {
                loaderTicket = ticket;
                loaderTicket.bindEntity(this);
                loaderTicket.getModData();
            }
            ForgeChunkManager.forceChunk(loaderTicket, new ChunkPos(chunkCoordX, chunkCoordZ));
        }
    }

    List<ChunkPos> loadedChunks = new ArrayList<ChunkPos>();

    public void loadNeighboringChunks(int newChunkX, int newChunkZ) {
        if (!world.isRemote && loaderTicket != null) {

            clearChunkLoader();

            loadedChunks.clear();
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ));

            // ChunkCoordIntPair doesnt exist in 1.12.2
            // loadedChunks.add(new ChunkCoordIntPair(newChunkX + (int) Math.floor((this.posX +
            // this.motionX) / 16D), newChunkZ + (int) Math.floor((this.posZ + this.motionZ) / 16D)));

            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.forceChunk(loaderTicket, chunk);
            }
        }
    }

    public void killAndClear() {
        this.setDead();
        this.clearChunkLoader();
    }

    public void clearChunkLoader() {
        if (!world.isRemote && loaderTicket != null) {
            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.unforceChunk(loaderTicket, chunk);
            }
        }
    }

    @Override
    protected float getAirDrag() {
        return 1.0F;
    }

    @Override
    public double getGravityVelocity() {
        return this.steering != null ? 0D : 0.01D;
    }

    @Override
    public RadarTargetType getTargetType() {
        return RadarTargetType.ARTILLERY;
    }
}