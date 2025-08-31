package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.ItemEnums;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.MagazineBelt;
import com.hbm.items.weapon.sedna.mags.MagazineFullReload;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.particle.SpentCasing;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.anim.sedna.BusAnimationKeyframeSedna.IType;
import com.hbm.render.anim.sedna.BusAnimationSedna;
import com.hbm.render.anim.sedna.BusAnimationSequenceSedna;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import com.hbm.render.misc.RenderScreenOverlay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory50 {
    public static BulletConfig bmg50_sp;
    public static BulletConfig bmg50_fmj;
    public static BulletConfig bmg50_jhp;
    public static BulletConfig bmg50_ap;
    public static BulletConfig bmg50_du;
    public static BulletConfig bmg50_he;

    public static BiConsumer<EntityBulletBaseMK4, RayTraceResult> LAMBDA_STANDARD_EXPLODE = (bullet, mop) -> {
        if(mop.typeOfHit == RayTraceResult.Type.ENTITY && bullet.ticksExisted < 3 && mop.entityHit == bullet.getThrower()) return;
        Lego.tinyExplode(bullet, mop, 2F); bullet.setDead();
    };

    public static void init() {
        SpentCasing casing762 = new SpentCasing(SpentCasing.CasingType.BOTTLENECK).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(1.5F);
        bmg50_sp = new BulletConfig().setItem(GunFactory.EnumAmmo.BMG50_SP).setCasing(ItemEnums.EnumCasingType.LARGE, 12)
                .setCasing(casing762.clone().register("bmg50"));
        bmg50_fmj = new BulletConfig().setItem(GunFactory.EnumAmmo.BMG50_FMJ).setCasing(ItemEnums.EnumCasingType.LARGE, 12).setDamage(0.8F).setThresholdNegation(7F).setArmorPiercing(0.1F)
                .setCasing(casing762.clone().register("bmg50fmj"));
        bmg50_jhp = new BulletConfig().setItem(GunFactory.EnumAmmo.BMG50_JHP).setCasing(ItemEnums.EnumCasingType.LARGE, 12).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
                .setCasing(casing762.clone().register("bmg50jhp"));
        bmg50_ap = new BulletConfig().setItem(GunFactory.EnumAmmo.BMG50_AP).setCasing(ItemEnums.EnumCasingType.LARGE_STEEL, 12).setDoesPenetrate(true).setDamageFalloutByPen(false).setDamage(1.5F).setThresholdNegation(17.5F).setArmorPiercing(0.15F)
                .setCasing(casing762.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50ap"));
        bmg50_du = new BulletConfig().setItem(GunFactory.EnumAmmo.BMG50_DU).setCasing(ItemEnums.EnumCasingType.LARGE_STEEL, 12).setDoesPenetrate(true).setDamageFalloutByPen(false).setDamage(2.5F).setThresholdNegation(21F).setArmorPiercing(0.25F)
                .setCasing(casing762.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50du"));
        bmg50_he = new BulletConfig().setItem(GunFactory.EnumAmmo.BMG50_HE).setCasing(ItemEnums.EnumCasingType.LARGE_STEEL, 12).setWear(3F).setDoesPenetrate(true).setDamageFalloutByPen(false).setDamage(1.75F).setOnImpact(LAMBDA_STANDARD_EXPLODE)
                .setCasing(casing762.clone().setColor(SpentCasing.COLOR_CASE_44).register("bmg50he"));

        ModItems.gun_m2 = new ItemGunBaseNT(ItemGunBaseNT.WeaponQuality.A_SIDE, "gun_m2", new GunConfig()
                .dura(3_000).draw(10).inspect(31).crosshair(RenderScreenOverlay.Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
                .rec(new Receiver(0)
                        .dmg(7.5F).delay(2).dry(10).auto(true).spread(0.005F).sound(HBMSoundHandler.chekhov_fire, 1.0F, 1.0F)
                        .mag(new MagazineBelt().addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_he))
                        .offset(1, -0.0625 * 2.5, -0.25D)
                        .setupStandardFire().recoil(LAMBDA_RECOIL_M2))
                .setupStandardConfiguration()
                .anim(LAMBDA_M2_ANIMS).orchestra(Orchestras.ORCHESTRA_M2)
        );
    }

    public static BiConsumer<ItemStack, ItemGunBaseNT.LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> Lego.handleStandardSmoke(ctx.entity, stack, 2000, 0.05D, 1.1D, 0);

    public static BiConsumer<ItemStack, ItemGunBaseNT.LambdaContext> LAMBDA_RECOIL_AMAT = (stack, ctx) -> ItemGunBaseNT.setupRecoil(12.5F, (float) (ctx.getPlayer().getRNG().nextGaussian() * 1));

    public static BiConsumer<ItemStack, ItemGunBaseNT.LambdaContext> LAMBDA_RECOIL_M2 = (stack, ctx) -> ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRNG().nextGaussian() * 0.5), (float) (ctx.getPlayer().getRNG().nextGaussian() * 0.5));

    @SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, HbmAnimationsSedna.AnimType, BusAnimationSedna> LAMBDA_M2_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimationSedna()
                .addBus("EQUIP", new BusAnimationSequenceSedna().addPos(80, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> new BusAnimationSedna()
                .addBus("RECOIL", new BusAnimationSequenceSedna().addPos(0, 0, -0.25, 25).addPos(0, 0, 0, 75));
        default -> null;
    };
}
