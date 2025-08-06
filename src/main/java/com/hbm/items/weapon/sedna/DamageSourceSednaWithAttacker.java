package com.hbm.items.weapon.sedna;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Locale;

public class DamageSourceSednaWithAttacker extends DamageSourceSednaNoAttacker {
    public Entity projectile;
    public Entity shooter;

    public DamageSourceSednaWithAttacker(String type, Entity projectile, Entity shooter) {
        super(type.toLowerCase(Locale.US));
        this.projectile = projectile;
        this.shooter = shooter;
    }

    @Override
    public Entity getImmediateSource() {
        return this.projectile;
    }

    @Override
    public Entity getTrueSource() {
        return this.shooter;
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase died) {
        ITextComponent diedName = died.getDisplayName();
        ITextComponent shooterName;

        if (shooter != null) {
            shooterName = shooter.getDisplayName();
        } else {
            shooterName = (ITextComponent) new TextComponentString("Unknown").getStyle().setObfuscated(true);
        }

        return new TextComponentTranslation("death.sedna." + this.damageType + ".attacker", diedName, shooterName);
    }
}
