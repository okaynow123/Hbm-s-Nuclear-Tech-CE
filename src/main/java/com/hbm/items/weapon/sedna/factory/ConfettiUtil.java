package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.mob.EntityCyberCrab;
import com.hbm.entity.mob.EntityTaintCrab;
import com.hbm.entity.mob.EntityTeslaCrab;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.util.DamageResistanceHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.Locale;

public class ConfettiUtil {

    public static void decideConfetti(EntityLivingBase entity, DamageSource source) {
        if(entity.isEntityAlive()) return;
//        if(source.damageType.equals(DamageResistanceHandler.DamageClass.LASER.name().toLowerCase(Locale.US))) pulverize(entity);
//        if(source.damageType.equals(DamageResistanceHandler.DamageClass.ELECTRIC.name().toLowerCase(Locale.US))) pulverize(entity);
        if(source.isExplosion()) gib(entity);
        //if(source.isFireDamage()) cremate(entity);
    }

    //Fuck me, ill need to port over those too now
//    public static void pulverize(EntityLivingBase entity) {
//        int amount = MathHelper.clamp_int((int) (entity.width * entity.height * entity.width * 25), 5, 50);
//        AshesCreator.composeEffect(entity.worldObj, entity, amount, 0.125F);
//        SkeletonCreator.composeEffect(entity.worldObj, entity, 1F);
//        entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "hbm:weapon.fire.disintegration", 2.0F, 0.9F + entity.getRNG().nextFloat() * 0.2F);
//    }

//    public static void cremate(EntityLivingBase entity) {
//        int amount = MathHelper.clamp_int((int) (entity.width * entity.height * entity.width * 25), 5, 50);
//        AshesCreator.composeEffect(entity.worldObj, entity, amount, 0.125F);
//        SkeletonCreator.composeEffect(entity.worldObj, entity, 0.25F);
//        entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "hbm:weapon.fire.disintegration", 2.0F, 0.9F + entity.getRNG().nextFloat() * 0.2F);
//    }

    public static void gib(EntityLivingBase entity) {
        if(entity instanceof EntityCyberCrab) return;
        if(entity instanceof EntityTeslaCrab) return;
        if(entity instanceof EntityTaintCrab) return;
        if(entity instanceof EntitySkeleton) return;
        if(entity instanceof EntitySlime) return;

        NBTTagCompound vdat = new NBTTagCompound();
        vdat.setString("type", "giblets");
        vdat.setInteger("ent", entity.getEntityId());
        PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(vdat, entity.posX, entity.posY + entity.height * 0.5, entity.posZ), new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY + entity.height * 0.5, entity.posZ, 150));
        entity.world.playSound(
                null,
                entity.posX, entity.posY, entity.posZ,
                SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD,
                SoundCategory.HOSTILE,
                2.0F,
                0.95F + entity.getRNG().nextFloat() * 0.2F
        );

    }
}
