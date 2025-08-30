package com.hbm.blocks.fluid;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.AdvancementManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;


public class GenericFluidBlock extends BlockFluidClassic {
    protected final Random rand = new Random();

    public float damage;
    public DamageSource damageSource;

    public GenericFluidBlock(Fluid fluid, Material material, String s) {
        super(fluid, material);
        this.setTranslationKey(s);
        this.setRegistryName(s);
        displacements.put(this, false);
        ModBlocks.ALL_BLOCKS.add(this);
    }

    public GenericFluidBlock setDamage(DamageSource source, float amount) {
        this.damageSource = source;
        this.damage = amount;
        return this;
    }

    @Override
    public void onEntityCollision(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Entity entity) {

        if (damageSource == null) return;

        if (entity instanceof EntityItem ei) {
            entity.motionX = 0;
            entity.motionY = 0;
            entity.motionZ = 0;

            if (entity.ticksExisted % 20 == 0 && !world.isRemote) {
                entity.attackEntityFrom(damageSource, damage * 0.1F);

                if (entity.isDead) {
                    if (!ei.getItem().isEmpty() && ei.getItem().getItem() == Items.SLIME_BALL) {
                        AxisAlignedBB box = entity.getEntityBoundingBox().grow(10.0D);
                        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, box);
                        for (EntityPlayer p : players) {
                            if (p instanceof EntityPlayerMP mp) {
                                AdvancementManager.grantAchievement(mp, AdvancementManager.achSulfuric);
                            }
                        }
                    }
                }
            }

            if (entity.ticksExisted % 5 == 0 && world.isRemote) {
                world.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D);
            }
        } else {
            if (entity.motionY < -0.2D) {
                entity.motionY *= 0.5D;
            }
            if (!world.isRemote) {
                entity.attackEntityFrom(damageSource, damage);
            }
        }

        if (entity.ticksExisted % 5 == 0) {
            world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.2F, 1.0F);
        }
    }
}
