package com.hbm.util;

import com.hbm.handler.ArmorModHandler;
import com.hbm.interfaces.Untested;
import com.hbm.items.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Untested
//OH man this will be a goldmine of bugs
public class EntityDamageUtil {

    public static boolean attackEntityFromIgnoreIFrame(Entity victim, DamageSource src, float damage) {

        if (!victim.attackEntityFrom(src, damage)) {
            float dmg = damage + ((EntityLivingBase) victim).lastDamage;
            return victim.attackEntityFrom(src, dmg);
        } else {
            return true;
        }
    }

    // mlbv: yes this is empty
    public static void damageArmorNT(EntityLivingBase living, float amount) {
    }

    public static float getLastDamage(Entity victim) {
        try {
            return ((EntityLivingBase) victim).lastDamage;
        } catch (Exception x) {
            return 0F;
        }
    }

    public static boolean wasAttackedByV1(DamageSource source) {

        if (source instanceof EntityDamageSource) {
            Entity attacker = ((EntityDamageSource) source).getImmediateSource();

            if (attacker instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) attacker;
                ItemStack chestplate = player.inventory.armorInventory.get(2);

                if (chestplate != null && ArmorModHandler.hasMods(chestplate)) {
                    ItemStack[] mods = ArmorModHandler.pryMods(chestplate);

                    if (mods[ArmorModHandler.extra] != null && mods[ArmorModHandler.extra].getItem() == ModItems.v1) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean attackEntityFromNT(EntityLivingBase living, DamageSource source, float amount, boolean ignoreIFrame, boolean allowSpecialCancel, double knockbackMultiplier, float pierceDT, float pierce) {
        if (living instanceof EntityPlayerMP playerMP && source.getTrueSource() instanceof EntityPlayer attacker) {
            if (!playerMP.canAttackPlayer(attacker))
                return false; //handles wack-ass no PVP rule as well as scoreboard friendly fire
        }
        DamageResistanceHandler.setup(pierceDT, pierce);
        living.attackEntityFrom(source, 0F);
        boolean ret = attackEntityFromNTInternal(living, source, amount, ignoreIFrame, allowSpecialCancel, knockbackMultiplier);
        DamageResistanceHandler.reset();
        return ret;
    }

    public static void setBeenAttacked(EntityLivingBase living) {
        living.velocityChanged = living.getRNG().nextDouble() >= living.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
    }

    private static boolean attackEntityFromNTInternal(EntityLivingBase living, DamageSource source, float amount, boolean ignoreIFrame, boolean allowSpecialCancel, double knockbackMultiplier) {
        if (ForgeHooks.onLivingAttack(living, source, amount) && allowSpecialCancel) return false;
        if (living.isEntityInvulnerable(source)) return false;
        if (living.world.isRemote) return false;
        if (living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.disableDamage && !source.canHarmInCreative())
            return false;

        setIdleTime(living, 0);
        if (living.getHealth() <= 0.0F) return false;
        if (source.isFireDamage() && living.isPotionActive(MobEffects.FIRE_RESISTANCE)) return false;

        living.limbSwingAmount = 1.5F;
        boolean didAttackRegister = true;

        if (living.hurtResistantTime > living.maxHurtResistantTime / 2.0F && !ignoreIFrame) {
            if (amount <= living.lastDamage) {
                return false;
            }
            damageEntityNT(living, source, amount - living.lastDamage);
            living.lastDamage = amount;
            didAttackRegister = false;
        } else {
            living.lastDamage = amount;
            //living.prevHealth = living.getHealth(); I believe this safe to ommit?
            living.hurtResistantTime = living.maxHurtResistantTime;
            damageEntityNT(living, source, amount);
            living.hurtTime = living.maxHurtTime = 10;
        }

        living.attackedAtYaw = 0.0F;
        Entity entity = source.getTrueSource();

        if (entity != null) {
            if (entity instanceof EntityLivingBase) {
                living.setRevengeTarget((EntityLivingBase) entity);
            }

            if (entity instanceof EntityPlayer) {
                setRecentlyHit(living, 100);
                setAttackingPlayer(living, (EntityPlayer) entity);

            } else if (entity instanceof EntityTameable) {
                EntityTameable entitywolf = (EntityTameable) entity;

                if (entitywolf.isTamed()) {
                    setRecentlyHit(living, 100);
                    setAttackingPlayer(living, null); //Null? I HOPE this wont cause NPEs?
                }
            }
        }

        if (didAttackRegister) {
            living.world.setEntityState(living, (byte) 2);

            if (source != DamageSource.DROWN) setBeenAttacked(living); //#

            if (entity != null) {
                double deltaX = entity.posX - living.posX;
                double deltaZ;

                for (deltaZ = entity.posZ - living.posZ; deltaX * deltaX + deltaZ * deltaZ < 1.0E-4D; deltaZ = (Math.random() - Math.random()) * 0.01D) {
                    deltaX = (Math.random() - Math.random()) * 0.01D;
                }

                living.attackedAtYaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - living.rotationYaw;
                if (knockbackMultiplier > 0) knockBack(living, entity, amount, deltaX, deltaZ, knockbackMultiplier);
            } else {
                living.attackedAtYaw = (float) ((int) (Math.random() * 2.0D) * 180);
            }
        }

        SoundEvent sound;

        if (living.getHealth() <= 0.0F) {
            sound = getDeathSound(living);
            if (didAttackRegister && sound != null)
                living.playSound(sound, getSoundVolume(living), getSoundPitch(living)); //#
            living.onDeath(source);
        } else {
            sound = getHurtSound(living);
            if (didAttackRegister && sound != null)
                living.playSound(sound, getSoundVolume(living), getSoundPitch(living)); //#
        }

        return true;
    }

    public static void damageEntityNT(EntityLivingBase living, DamageSource source, float amount) {
        if (!living.isEntityInvulnerable(source)) {
            amount = ForgeHooks.onLivingHurt(living, source, amount);
            if (amount <= 0) return;

            amount = applyArmorCalculationsNT(living, source, amount);
            amount = applyPotionDamageCalculations(living, source, amount);

            float originalAmount = amount;
            amount = Math.max(amount - living.getAbsorptionAmount(), 0.0F);
            living.setAbsorptionAmount(living.getAbsorptionAmount() - (originalAmount - amount));

            if (amount != 0.0F) {
                float health = living.getHealth();
                living.setHealth(health - amount);
                living.getCombatTracker().trackDamage(source, health, amount);
                living.setAbsorptionAmount(living.getAbsorptionAmount() - amount);
            }
        }
    }

    public static float applyArmorCalculationsNT(EntityLivingBase living, DamageSource source, float amount) {
        if (!source.isUnblockable()) {
            float i = 25F - (living.getTotalArmorValue() * (1 - DamageResistanceHandler.currentPDR));
            float armor = amount * (float) i;
            damageArmorNT(living, amount);
            amount = armor / 25.0F;
        }

        return amount;
    }


    public static float applyPotionDamageCalculations(EntityLivingBase living, DamageSource source, float amount) {
        if (source.isDamageAbsolute()) {
            return amount;
        } else {

            int resistance;
            int j;
            float f1;

            if (living.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
                resistance = (living.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                j = 25 - resistance;
                f1 = amount * (float) j;
                amount = f1 / 25.0F;
            }

            if (amount <= 0.0F) {
                return 0.0F;
            } else {

                resistance = EnchantmentHelper.getEnchantmentModifierDamage(living.getArmorInventoryList(), source);

                if (resistance > 20) {
                    resistance = 20;
                }

                if (resistance > 0 && resistance <= 20) {
                    j = 25 - resistance;
                    f1 = amount * (float) j;
                    amount = f1 / 25.0F;
                }

                return amount;
            }
        }
    }

    public static void knockBack(EntityLivingBase living, Entity attacker, float damage, double motionX, double motionZ, double multiplier) {
        if (living.getRNG().nextDouble() >= living.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()) {
            living.isAirBorne = true;
            double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
            double magnitude = 0.4D * multiplier;
            living.motionX /= 2.0D;
            living.motionY /= 2.0D;
            living.motionZ /= 2.0D;
            living.motionX -= motionX / horizontal * magnitude;
            living.motionY += (double) magnitude;
            living.motionZ -= motionZ / horizontal * magnitude;

            if (living.motionY > 0.2D) {
                living.motionY = 0.2D * multiplier;
            }
        }
    }

    public static SoundEvent getDeathSound(EntityLivingBase living) {
        Method m = ReflectionHelper.findMethod(EntityLivingBase.class, "getDeathSound", "func_70673_aS");
        try {
            return (SoundEvent) m.invoke(living);
        } catch (Exception e) {
        }
        return SoundEvents.ENTITY_GENERIC_DEATH;
    }

    public static SoundEvent getHurtSound(EntityLivingBase living) {
        Method m = ReflectionHelper.findMethod(EntityLivingBase.class, "getHurtSound", "func_70621_aR");
        try {
            return (SoundEvent) m.invoke(living);
        } catch (Exception e) {
        }
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    public static float getSoundVolume(EntityLivingBase living) {
        Method m = ReflectionHelper.findMethod(EntityLivingBase.class, "getSoundVolume", "func_70599_aP");
        try {
            return (float) m.invoke(living);
        } catch (Exception e) {
        }
        return 1F;
    }

    public static float getSoundPitch(EntityLivingBase living) {
        Method m = ReflectionHelper.findMethod(EntityLivingBase.class, "getSoundPitch", "func_70647_i");
        try {
            return (float) m.invoke(living);
        } catch (Exception e) {
        }
        return 1F;
    }

    public static void setRecentlyHit(EntityLivingBase living, int amount) {
        Field field = ReflectionHelper.findField(EntityLivingBase.class, "recentlyHit", "field_70718_bc");
        try {
            field.set(living, amount);
        } catch (Exception e) {
        }


    }
    public static void setAttackingPlayer(EntityLivingBase living, EntityPlayer attackingPlayer) {
        Field field = ReflectionHelper.findField(EntityLivingBase.class, "attackingPlayer", "field_70717_bb");
        try {
            field.set(living, attackingPlayer);
        } catch (Exception e) {
        }
    }
    public static void setIdleTime(EntityLivingBase living, int value) {
        Field field = ReflectionHelper.findField(EntityLivingBase.class, "idleTime", "field_70708_bq");
        try {
            field.set(living, value);
        } catch (Exception e) {
        }
    }


}
