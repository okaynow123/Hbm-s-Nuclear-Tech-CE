package com.hbm.items.weapon;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.projectile.EntityArtilleryRocket;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import com.hbm.potion.HbmPotion;
import com.hbm.render.amlfrom1710.Vec3;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemAmmoHIMARS extends Item implements IMetaItemTesr {
    public static HIMARSRocket[] itemTypes = new HIMARSRocket[8];

    public static final int SMALL = 0;
    public static final int LARGE = 1;
    public static final int SMALL_HE = 2;
    public static final int SMALL_WP = 3;
    public static final int SMALL_TB = 4;
    public static final int LARGE_TB = 5;
    public static final int SMALL_MINI_NUKE = 6;
    public static final int SMALL_LAVA = 7;

    public ItemAmmoHIMARS(String s) {
        this.setTranslationKey(s);
        this.setRegistryName(s);
        this.setHasSubtypes(true);
        this.setCreativeTab(MainRegistry.weaponTab);
        this.setMaxStackSize(1);

        init();
        ModItems.ALL_ITEMS.add(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < itemTypes.length; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }



    @Override
    public void addInformation(
            ItemStack stack, World worldIn, @NotNull List<String> list, @NotNull ITooltipFlag flagIn) {

        switch (stack.getItemDamage()) {
            case SMALL:
                list.add(TextFormatting.YELLOW + "Strength: 20");
                list.add(TextFormatting.YELLOW + "Damage modifier: 3x");
                list.add(TextFormatting.BLUE + "Does not destroy blocks");
                break;
            case SMALL_HE:
                list.add(TextFormatting.YELLOW + "Strength: 20");
                list.add(TextFormatting.YELLOW + "Damage modifier: 3x");
                list.add(TextFormatting.RED + "Destroys blocks");
                break;
            case SMALL_WP:
                list.add(TextFormatting.YELLOW + "Strength: 20");
                list.add(TextFormatting.YELLOW + "Damage modifier: 3x");
                list.add(TextFormatting.RED + "Phosphorus splash");
                list.add(TextFormatting.BLUE + "Does not destroy blocks");
                break;
            case SMALL_TB:
                list.add(TextFormatting.YELLOW + "Strength: 20");
                list.add(TextFormatting.YELLOW + "Damage modifier: 10x");
                list.add(TextFormatting.RED + "Destroys blocks");
                break;
            case SMALL_MINI_NUKE:
                list.add(TextFormatting.YELLOW + "Strength: 20");
                list.add(TextFormatting.RED + "Deals nuclear damage");
                list.add(TextFormatting.RED + "Destroys blocks");
                break;
            case SMALL_LAVA:
                list.add(TextFormatting.YELLOW + "Strength: 20");
                list.add(TextFormatting.RED + "Creates volcanic lava");
                list.add(TextFormatting.RED + "Destroys blocks");
                break;
            case LARGE:
                list.add(TextFormatting.YELLOW + "Strength: 50");
                list.add(TextFormatting.YELLOW + "Damage modifier: 5x");
                list.add(TextFormatting.RED + "Destroys blocks");
                break;
            case LARGE_TB:
                list.add(TextFormatting.YELLOW + "Strength: 50");
                list.add(TextFormatting.YELLOW + "Damage modifier: 12x");
                list.add(TextFormatting.RED + "Destroys blocks");
                break;
        }
    }

    @Override
    public @NotNull String getTranslationKey(ItemStack stack) {
        return "item." + itemTypes[Math.abs(stack.getItemDamage()) % itemTypes.length].ammo_name;
    }

    @Override
    public int getSubitemCount() {
        return itemTypes.length;
    }

    public abstract static class HIMARSRocket {
        public enum Type {
            Standard,
            Single
        }

        public final String ammo_name;
        public final String name;
        public final ResourceLocation texture;
        public final int amount;
        public final Type modelType;

        public HIMARSRocket(String name, Type type) {
            this.ammo_name = "ammo_himars_" + name;
            this.name = name;
            this.texture =
                    new ResourceLocation(
                            RefStrings.MODID, "textures/models/projectiles/himars_" + name + ".png");
            this.amount = type == Type.Standard ? 6 : 1;
            this.modelType = type;
        }

        public abstract void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop);
        public void onUpdate(EntityArtilleryRocket rocket) {}
    }

    public static void standardExplosion(
            EntityArtilleryRocket rocket,
            RayTraceResult mop,
            float size,
            float rangeMod,
            boolean breaksBlocks,
            Block slag,
            int slagMeta) {
        Vec3 vec = Vec3.createVectorHelper(rocket.motionX, rocket.motionY, rocket.motionZ).normalize();
        ExplosionVNT xnt =
                new ExplosionVNT(
                        rocket.world,
                        mop.hitVec.x - vec.xCoord,
                        mop.hitVec.y - vec.yCoord,
                        mop.hitVec.z - vec.zCoord,
                        size);
        if (breaksBlocks) {
            xnt.setBlockAllocator(new BlockAllocatorStandard(48));
            xnt.setBlockProcessor(
                    new BlockProcessorStandard()
                            .setNoDrop()
                            .withBlockEffect(new BlockMutatorDebris(slag, slagMeta)));
        }
        xnt.setEntityProcessor(new EntityProcessorCross(7.5D).withRangeMod(rangeMod));
        xnt.setPlayerProcessor(new PlayerProcessorStandard());
        xnt.setSFX(new ExplosionEffectStandard());
        xnt.explode();
        rocket.killAndClear();
    }

    public static void standardMush(EntityArtilleryRocket rocket, RayTraceResult mop, float size) {
        NBTTagCompound data = new NBTTagCompound();
        data.setString("type", "rbmkmush");
        data.setFloat("scale", size);
        PacketDispatcher.wrapper.sendToAllAround(
                new AuxParticlePacketNT(data, mop.hitVec.x, mop.hitVec.y, mop.hitVec.z),
                new NetworkRegistry.TargetPoint(
                        rocket.dimension, rocket.posX, rocket.posY, rocket.posZ, 250));
    }

    private void init() {
        /* STANDARD ROCKETS */
        itemTypes[SMALL] =
                new HIMARSRocket("standard", HIMARSRocket.Type.Standard) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        final BlockPos blockPos = mop.getBlockPos();

                        standardExplosion(rocket, mop, 20F, 3F, false, ModBlocks.block_slag, 1);
                        //            ExplosionCreator.composeEffect(
                        //                rocket.world,
                        //                blockPos.getX() + 0.5,
                        //                blockPos.getY() + 0.5,
                        //                blockPos.getZ() + 0.5,
                        //                15,
                        //                5F,
                        //                1F,
                        //                45F,
                        //                10,
                        //                0,
                        //                50,
                        //                1F,
                        //                3F,
                        //                -2F,
                        //                200);
                    }
                };

        itemTypes[SMALL_HE] =
                new HIMARSRocket("standard_he", HIMARSRocket.Type.Standard) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        final BlockPos blockPos = mop.getBlockPos();

                        standardExplosion(rocket, mop, 20F, 3F, true, ModBlocks.block_slag, 1);
                        //            ExplosionCreator.composeEffect(
                        //                rocket.world,
                        //                blockPos.getX() + 0.5,
                        //                blockPos.getY() + 0.5,
                        //                blockPos.getZ() + 0.5,
                        //                15,
                        //                5F,
                        //                1F,
                        //                45F,
                        //                10,
                        //                16,
                        //                50,
                        //                1F,
                        //                3F,
                        //                -2F,
                        //                200);
                    }
                };
        itemTypes[SMALL_LAVA] =
                new HIMARSRocket("standard_lava", HIMARSRocket.Type.Standard) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        standardExplosion(rocket, mop, 20F, 3F, true, ModBlocks.volcanic_lava_block, 0);
                    }
                };
        itemTypes[LARGE] =
                new HIMARSRocket("single", HIMARSRocket.Type.Single) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        final BlockPos blockPos = mop.getBlockPos();

                        standardExplosion(rocket, mop, 50F, 5F, true, ModBlocks.block_slag, 1);
                        //            ExplosionCreator.composeEffect(
                        //                rocket.world,
                        //                blockPos.getX() + 0.5,
                        //                blockPos.getY() + 0.5,
                        //                blockPos.getZ() + 0.5,
                        //                30,
                        //                6.5F,
                        //                2F,
                        //                65F,
                        //                25,
                        //                16,
                        //                50,
                        //                1.25F,
                        //                3F,
                        //                -2F,
                        //                350);
                    }
                };

        itemTypes[SMALL_MINI_NUKE] =
                new HIMARSRocket("standard_mini_nuke", HIMARSRocket.Type.Standard) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        rocket.killAndClear();
                        Vec3 vec =
                                Vec3.createVectorHelper(rocket.motionX, rocket.motionY, rocket.motionZ).normalize();
                        //            ExplosionNukeGeneric.explode(
                        //                rocket.world,
                        //                mop.hitVec.x - vec.xCoord,
                        //                mop.hitVec.y - vec.yCoord,
                        //                mop.hitVec.z - vec.zCoord,
                        //                ExplosionNukeSmall.PARAMS_MEDIUM);
                        //            rocket.world.spawnEntity(EntityNukeExplosionMK5.statFac(rocket.world, 100,
                        // mop.hitVec.x, mop.hitVec.y, mop.hitVec.z));
                        //            EntityNukeTorex.statFac(rocket.world, mop.hitVec.x, mop.hitVec.y,
                        // mop.hitVec.z, 100);
                        //            rocket.setDead();
                    }
                };

        itemTypes[SMALL_WP] =
                new HIMARSRocket("standard_wp", HIMARSRocket.Type.Standard) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        rocket.world.playSound(
                                null,
                                rocket.posX,
                                rocket.posY,
                                rocket.posZ,
                                HBMSoundHandler.explosion_medium,
                                SoundCategory.BLOCKS,
                                20.0F,
                                0.9F + rocket.world.rand.nextFloat() * 0.2F);
                        standardExplosion(rocket, mop, 20F, 3F, false, ModBlocks.block_slag, 1);
                        ExplosionLarge.spawnShrapnels(
                                rocket.world, (int) mop.hitVec.x, (int) mop.hitVec.y, (int) mop.hitVec.z, 30);
                        ExplosionChaos.burn(
                                rocket.world,
                                new BlockPos((int) mop.hitVec.x, (int) mop.hitVec.y, (int) mop.hitVec.z),
                                20);
                        int radius = 30;
                        List<Entity> hit =
                                rocket.world.getEntitiesWithinAABBExcludingEntity(
                                        rocket,
                                        new AxisAlignedBB(
                                                rocket.posX - radius,
                                                rocket.posY - radius,
                                                rocket.posZ - radius,
                                                rocket.posX + radius,
                                                rocket.posY + radius,
                                                rocket.posZ + radius));
                        for (Entity e : hit) {
                            e.setFire(5);
                            if (e instanceof EntityLivingBase) {
                                PotionEffect eff =
                                        new PotionEffect(HbmPotion.phosphorus.delegate.get(), 30 * 20, 0, true, false);
                                eff.getCurativeItems().clear();
                                ((EntityLivingBase) e).addPotionEffect(eff);
                            }
                        }
                        for (int i = 0; i < 10; i++) {
                            NBTTagCompound haze = new NBTTagCompound();
                            haze.setString("type", "haze");
                            PacketDispatcher.wrapper.sendToAllAround(
                                    new AuxParticlePacketNT(
                                            haze,
                                            mop.hitVec.x + rocket.world.rand.nextGaussian() * 15,
                                            mop.hitVec.y,
                                            mop.hitVec.z + rocket.world.rand.nextGaussian() * 15),
                                    new NetworkRegistry.TargetPoint(
                                            rocket.dimension, rocket.posX, rocket.posY, rocket.posZ, 150));
                        }
                        standardMush(rocket, mop, 15);
                    }
                };

        itemTypes[SMALL_TB] =
                new HIMARSRocket("standard_tb", HIMARSRocket.Type.Standard) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        rocket.world.playSound(
                                null,
                                rocket.posX,
                                rocket.posY,
                                rocket.posZ,
                                HBMSoundHandler.explosion_medium,
                                SoundCategory.BLOCKS,
                                20.0F,
                                0.9F + rocket.world.rand.nextFloat() * 0.2F);
                        standardExplosion(rocket, mop, 20F, 10F, true, ModBlocks.block_slag, 1);
                        ExplosionLarge.spawnShrapnels(
                                rocket.world, (int) mop.hitVec.x, (int) mop.hitVec.y, (int) mop.hitVec.z, 30);
                        standardMush(rocket, mop, 20);
                    }
                };

        itemTypes[LARGE_TB] =
                new HIMARSRocket("single_tb", HIMARSRocket.Type.Single) {
                    public void onImpact(EntityArtilleryRocket rocket, RayTraceResult mop) {
                        rocket.world.playSound(
                                null,
                                rocket.posX,
                                rocket.posY,
                                rocket.posZ,
                                HBMSoundHandler.explosion_medium,
                                SoundCategory.BLOCKS,
                                20.0F,
                                0.9F + rocket.world.rand.nextFloat() * 0.2F);
                        standardExplosion(rocket, mop, 50F, 12F, true, ModBlocks.block_slag, 1);
                        ExplosionLarge.spawnShrapnels(
                                rocket.world, (int) mop.hitVec.x, (int) mop.hitVec.y, (int) mop.hitVec.z, 30);
                        standardMush(rocket, mop, 35);
                    }
                };
    }
}
