package com.hbm.entity.item;

import com.hbm.api.block.IFuckingExplode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTNTPrimedBase extends Entity {

    private static final DataParameter<Integer> FUSE = EntityDataManager.<Integer>createKey(EntityTNTPrimedBase.class, DataSerializers.VARINT); // Fuse time in ticks
    private static final DataParameter<String> TILE = EntityDataManager.<String>createKey(EntityTNTPrimedBase.class, DataSerializers.STRING); //Block's registry name (INCLUDES THE MODID)
    private static final DataParameter<Byte> META = EntityDataManager.<Byte>createKey(EntityTNTPrimedBase.class, DataSerializers.BYTE); //Block's meta


    public int fuse;
    @SideOnly(Side.CLIENT)
    public IBlockState state;


    public boolean detonateOnCollision;
    private EntityLivingBase tntPlacedBy;

    public EntityTNTPrimedBase(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.fuse = 80;
        this.detonateOnCollision = false;
    }


    public EntityTNTPrimedBase(World world, double x, double y, double z, EntityLivingBase entity, IBlockState bomb) {
        this(world);
        this.setPosition(x, y, z);
        float f = (float) (Math.random() * Math.PI * 2.0D);
        this.motionX = (double) (-((float) Math.sin((double) f)) * 0.02F);
        this.motionY = 0.2D;
        this.motionZ = (double) (-((float) Math.cos((double) f)) * 0.02F);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.tntPlacedBy = entity;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(FUSE, 80);
        this.dataManager.register(TILE, "minecraft:tnt");
        this.dataManager.register(META, (byte) 0);

    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public double getYOffset() { //Replacement to yOffset variable
        return this.height / 2.0F;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.04D;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;

        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
            this.motionY *= -0.5D;
        }

        if (this.fuse-- <= 0 || (this.detonateOnCollision && this.collided)) {
            this.setDead();

            if (!this.world.isRemote) {
                this.explode();
            }
        } else {
            this.handleWaterMovement();
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode() {
        this.getBomb().explodeEntity(world, posX, posY, posZ, this);
    }

    public IFuckingExplode getBomb() {
        return (IFuckingExplode) getState();
    }

    public IBlockState getState() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dataManager.get(TILE))).getStateFromMeta(dataManager.get(META));
    }

    public void setState(IBlockState state) {
        dataManager.set(TILE, ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
        dataManager.set(META, (byte) state.getBlock().getMetaFromState(state));
    }

    public void setState(String loc, byte meta) {
        state = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(loc)).getStateFromMeta(meta);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("fuse", (short) this.fuse);
        nbt.setString("tile", ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
        nbt.setByte("meta", (byte) state.getBlock().getMetaFromState(state));

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.setFuse(nbt.getShort("Fuse"));
        this.setState(nbt.getString("tile"), nbt.getByte("meta"));
    }


    public EntityLivingBase getTntPlacedBy() {
        return this.tntPlacedBy;
    }


    public int getFuse() {
        return this.fuse;
    }

    public void setFuse(int fuseIn) {
        this.dataManager.set(FUSE, Integer.valueOf(fuseIn));
        this.fuse = fuseIn;
    }


    public float getEyeHeight() {
        return 0.0F;
    }
}
