package com.hbm.entity.effect;

import com.hbm.capability.HbmLivingCapability;
import com.hbm.capability.HbmLivingProps;
import com.hbm.interfaces.AutoRegister;
import com.hbm.particle.helper.FlameCreator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@AutoRegister(name = "entity_fire_lingering")
public class EntityFireLingering extends Entity {
    public static int TYPE_DIESEL = 0;
    public static int TYPE_BALEFIRE = 1;
    public static int TYPE_PHOSPHORUS = 2;
    public int maxAge = 150;

    public static final DataParameter<Float> WIDTH = EntityDataManager.createKey(EntityFireLingering.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(EntityFireLingering.class, DataSerializers.FLOAT);
    public static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityFireLingering.class, DataSerializers.VARINT);

    public EntityFireLingering(World world) {
        super(world);
    }

    public EntityFireLingering setArea(float width, float height) {
        this.getDataManager().set(WIDTH, width);
        this.getDataManager().set(HEIGHT, height);
        return this;
    }
    public EntityFireLingering setDuration(int duration){
        this.maxAge = duration;
        return this;
    }

    @Override
    protected void entityInit() {
        this.getDataManager().register(TYPE, new Integer(0));
        this.getDataManager().register(WIDTH, new Float(0));
        this.getDataManager().register(HEIGHT, new Float(0));
    }

    public EntityFireLingering setType(int type) {
        this.getDataManager().set(TYPE, type);
        return this;
    }

    public int getType() {
        return this.getDataManager().get(TYPE);
    }

    @Override
    public void onEntityUpdate() {

        float height = this.getDataManager().get(HEIGHT);
        this.setSize(this.getDataManager().get(WIDTH), height);
        this.setPosition(this.posX, this.posY, this.posZ);

        if(!world.isRemote) {

            if(this.ticksExisted >= maxAge) {
                this.setDead();
            }

            List<Entity> affected = world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(posX - width / 2, posY, posZ - width / 2, posX + width / 2, posY + height, posZ + width / 2));

            for(Entity e : affected) {
                if(e instanceof EntityLivingBase) {
                    EntityLivingBase livng = (EntityLivingBase) e;
                    HbmLivingCapability.IEntityHbmProps props = HbmLivingProps.getData(livng);
                    if(this.getType() == this.TYPE_DIESEL) if(props.getFire() < 60) props.setFire(60);
                    if(this.getType() == this.TYPE_PHOSPHORUS) if(props.getFire() < 300) props.setFire(300);
                    //if(this.getType() == this.TYPE_BALEFIRE) if(props.balefire < 100) props.balefire = 100;
                } else {
                    e.setFire(4);
                }
            }
        } else {

            for(int i = 0; i < (width >= 5 ? 2 : 1); i++) {
                double x = posX - width / 2 + rand.nextDouble() * width;
                double z = posZ - width / 2 + rand.nextDouble() * width;

                Vec3d up = new Vec3d(x, posY + height, z);
                Vec3d down = new Vec3d(x, posY - height, z);
                RayTraceResult mop = world.rayTraceBlocks(up, down, false, true, true);
                if(mop != null && mop.typeOfHit == mop.typeOfHit.BLOCK) down = mop.hitVec;
                if(this.getType() == this.TYPE_DIESEL) FlameCreator.composeEffectClient(world, x, down.y, z, FlameCreator.META_FIRE);
                if(this.getType() == this.TYPE_PHOSPHORUS) FlameCreator.composeEffectClient(world, x, down.y, z, FlameCreator.META_FIRE);
                if(this.getType() == this.TYPE_BALEFIRE) FlameCreator.composeEffectClient(world, x, down.y, z, FlameCreator.META_BALEFIRE);
            }
        }
    }

    @Override @SideOnly(Side.CLIENT) public boolean canRenderOnFire() { return false; }
    @Override protected void writeEntityToNBT(NBTTagCompound nbt) { }
    @Override public boolean writeToNBTOptional(NBTTagCompound nbt) { return false; }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return nbt; }
    @Override public void readEntityFromNBT(NBTTagCompound nbt) { this.setDead(); }
}
