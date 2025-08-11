package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.hud.HUDComponentAmmoCounter;
import com.hbm.items.weapon.sedna.hud.HUDComponentDurabilityBar;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;
import com.hbm.render.misc.BeamPronter;
import com.hbm.render.tileentity.RenderArcFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.function.BiConsumer;

public class LegoClient {

    public static HUDComponentDurabilityBar HUD_COMPONENT_DURABILITY = new HUDComponentDurabilityBar();
    public static HUDComponentDurabilityBar HUD_COMPONENT_DURABILITY_MIRROR = new HUDComponentDurabilityBar(true);
    public static HUDComponentAmmoCounter HUD_COMPONENT_AMMO = new HUDComponentAmmoCounter(0);
    public static HUDComponentAmmoCounter HUD_COMPONENT_AMMO_MIRROR = new HUDComponentAmmoCounter(0).mirror();
    public static HUDComponentAmmoCounter HUD_COMPONENT_AMMO_NOCOUNTER = new HUDComponentAmmoCounter(0).noCounter();

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_STANDARD_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0xFFBF00, 0xFFFFFF, length, false);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLECHETTE_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x8C8C8C, 0xCACACA, length, false);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_AP_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0xFF6A00, 0xFFE28D, length, false);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_EXPRESS_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x9E082E, 0xFF8A79, length, false);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_DU_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x5CCD41, 0xE9FF8D, length, false);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_TRACER_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x9E082E, 0xFF8A79, length, true);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_LEGENDARY_BULLET = (bullet, interp) -> {
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length <= 0) return;
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x7F006E, 0xFF7FED, length, true);
    };

    public static void renderBulletStandard(BufferBuilder buffer, int dark, int light, double length, boolean fullbright) {
        renderBulletStandard(buffer, dark, light, length, 0.03125D, 0.03125D * 0.25D, fullbright);
    }

    public static void renderBulletStandard(BufferBuilder buffer, int dark, int light, double length, double widthF, double widthB, boolean fullbright) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(1F, 1F, 1F, 1F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        if(fullbright) {
            buffer.lightmap(240, 240);
        }

        buffer.pos(length, widthB, -widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, widthB, widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(0, widthF, widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, widthF, -widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();

        buffer.pos(length, -widthB, -widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, -widthB, widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(0, -widthF, widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, -widthF, -widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();

        buffer.pos(length, -widthB, widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, widthB, widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(0, widthF, widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, -widthF, widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();

        buffer.pos(length, -widthB, -widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, widthB, -widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(0, widthF, -widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, -widthF, -widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();

        buffer.pos(length, widthB, widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, widthB, -widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, -widthB, -widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();
        buffer.pos(length, -widthB, widthB).color((dark >> 16) & 0xFF, (dark >> 8) & 0xFF, dark & 0xFF, 255).endVertex();

        buffer.pos(0, widthF, widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, widthF, -widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, -widthF, -widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();
        buffer.pos(0, -widthF, widthF).color((light >> 16) & 0xFF, (light >> 8) & 0xFF, light & 0xFF, 255).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
    }

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLARE = (bullet, interp) -> { renderFlare(bullet, interp, 1F, 0.5F, 0.5F); };
    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLARE_SUPPLY = (bullet, interp) -> { renderFlare(bullet, interp, 0.5F, 0.5F, 1F); };
    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLARE_WEAPON = (bullet, interp) -> { renderFlare(bullet, interp, 0.5F, 1F, 0.5F); };

    private static final ResourceLocation flare = new ResourceLocation(RefStrings.MODID + ":textures/particle/flare.png");
    public static void renderFlare(Entity bullet, float interp, float r, float g, float b) {

        if(bullet.ticksExisted < 2) return;
        RenderArcFurnace.fullbright(true);

        double scale = Math.min(5, (bullet.ticksExisted + interp - 2) * 0.5) * (0.8 + bullet.world.rand.nextDouble() * 0.4);
        renderFlareSprite(bullet, interp, r, g, b, scale, 0.5F, 0.75F);

        RenderArcFurnace.fullbright(false);
    }
    public static void renderFlareSprite(Entity bullet, float interp, float r, float g, float b, double scale, float outerAlpha, float innerAlpha) {

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        RenderHelper.disableStandardItemLighting();

        Minecraft.getMinecraft().getTextureManager().bindTexture(flare);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        float f1 = ActiveRenderInfo.getRotationX();
        float f2 = ActiveRenderInfo.getRotationZ();
        float f3 = ActiveRenderInfo.getRotationYZ();
        float f4 = ActiveRenderInfo.getRotationXY();
        float f5 = ActiveRenderInfo.getRotationXZ();

        double posX = 0.0D;
        double posY = 0.0D;
        double posZ = 0.0D;

        int or = (int) (r * 255.0F);
        int og = (int) (g * 255.0F);
        int ob = (int) (b * 255.0F);
        int oa = (int) (outerAlpha * 255.0F);

        buffer.pos(posX - f1 * scale - f3 * scale, posY - f5 * scale, posZ - f2 * scale - f4 * scale).tex(1.0D, 1.0D).color(or, og, ob, oa).endVertex();
        buffer.pos(posX - f1 * scale + f3 * scale, posY + f5 * scale, posZ - f2 * scale + f4 * scale).tex(1.0D, 0.0D).color(or, og, ob, oa).endVertex();
        buffer.pos(posX + f1 * scale + f3 * scale, posY + f5 * scale, posZ + f2 * scale + f4 * scale).tex(0.0D, 0.0D).color(or, og, ob, oa).endVertex();
        buffer.pos(posX + f1 * scale - f3 * scale, posY - f5 * scale, posZ + f2 * scale - f4 * scale).tex(0.0D, 1.0D).color(or, og, ob, oa).endVertex();

        scale *= 0.5D;

        int ia = (int) (innerAlpha * 255.0F);

        buffer.pos(posX - f1 * scale - f3 * scale, posY - f5 * scale, posZ - f2 * scale - f4 * scale).tex(1.0D, 1.0D).color(255, 255, 255, ia).endVertex();
        buffer.pos(posX - f1 * scale + f3 * scale, posY + f5 * scale, posZ - f2 * scale + f4 * scale).tex(1.0D, 0.0D).color(255, 255, 255, ia).endVertex();
        buffer.pos(posX + f1 * scale + f3 * scale, posY + f5 * scale, posZ + f2 * scale + f4 * scale).tex(0.0D, 0.0D).color(255, 255, 255, ia).endVertex();
        buffer.pos(posX + f1 * scale - f3 * scale, posY - f5 * scale, posZ + f2 * scale - f4 * scale).tex(0.0D, 1.0D).color(255, 255, 255, ia).endVertex();

        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_GRENADE = (bullet, interp) -> {
        GL11.glScalef(0.25F, 0.25F, 0.25F);
        GL11.glRotated(90, 0, 0, 1);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.grenade_tex);
        ResourceManager.projectiles.renderPart("Grenade");
        GL11.glShadeModel(GL11.GL_FLAT);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_BIG_NUKE = (bullet, interp) -> {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glRotated(90, 0, 0, 1);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.rocket_mirv_tex);
        ResourceManager.projectiles.renderPart("MissileMIRV");
        GL11.glShadeModel(GL11.GL_FLAT);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_RPZB = (bullet, interp) -> {

        GL11.glPushMatrix();
        GL11.glScalef(0.125F, 0.125F, 0.125F);
        GL11.glRotated(90, 0, -1, 0);
        GL11.glTranslatef(0, 0, 3.5F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.panzerschreck_tex);
        ResourceManager.panzerschreck.renderPart("Rocket");
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();

        GL11.glTranslatef(0.375F, 0, 0);
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length > 0) renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x808080, 0xFFF2A7, length * 2, true);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_QD = (bullet, interp) -> {

        GL11.glPushMatrix();
        GL11.glRotated(90, 0, 0, 1);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.rocket_tex);
        ResourceManager.projectiles.renderPart("Rocket");
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();

        GL11.glTranslatef(0.375F, 0, 0);
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length > 0) renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x808080, 0xFFF2A7, length * 2, true);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_ML = (bullet, interp) -> {

        GL11.glPushMatrix();
        GL11.glScalef(0.25F, 0.25F, 0.25F);
        GL11.glRotated(-90, 0, 1, 0);
        GL11.glTranslatef(0, -1, -4.5F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.missile_launcher_tex);
        ResourceManager.missile_launcher.renderPart("Missile");
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();

        GL11.glTranslatef(0.375F, 0, 0);
        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if(length > 0) renderBulletStandard(Tessellator.getInstance().getBuffer(), 0x808080, 0xFFF2A7, length * 2, true);
    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LIGHTNING = (bullet, interp) -> {

        RenderArcFurnace.fullbright(true);
        GL11.glPushMatrix();
        GL11.glRotatef(180 - bullet.rotationYaw, 0, 1F, 0);
        GL11.glRotatef(-bullet.rotationPitch - 90, 1F, 0, 0);
        Vec3d delta = new Vec3d(0, bullet.beamLength, 0);
        double age = MathHelper.clamp(1D - ((double) bullet.ticksExisted - 2 + interp) / (double) bullet.getBulletConfig().expires, 0, 1);
        GL11.glScaled(age / 2 + 0.5, 1, age / 2 + 0.5);
        double scale = 0.075D;
        int colorInner = ((int)(0x20 * age) << 16) | ((int)(0x20 * age) << 8) | (int) (0x40 * age);
        int colorOuter = ((int)(0x40 * age) << 16) | ((int)(0x40 * age) << 8) | (int) (0x80 * age);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorInner, colorInner, bullet.ticksExisted / 3, (int)(bullet.beamLength / 2 + 1), (float)scale * 1F, 4, 0.25F);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorOuter, colorOuter, bullet.ticksExisted, (int)(bullet.beamLength / 2 + 1), (float)scale * 7F, 2, 0.0625F);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorOuter, colorOuter, bullet.ticksExisted / 2, (int)(bullet.beamLength / 2 + 1), (float)scale * 7F, 2, 0.0625F);
        GL11.glPopMatrix();
        RenderArcFurnace.fullbright(false);
    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_TAU = (bullet, interp) -> {

        RenderArcFurnace.fullbright(true);
        double age = MathHelper.clamp(1D - ((double) bullet.ticksExisted - 2 + interp) / (double) bullet.getBulletConfig().expires, 0, 1);

        GL11.glPushMatrix();
        GL11.glRotatef(180 - bullet.rotationYaw, 0, 1F, 0);
        GL11.glRotatef(-bullet.rotationPitch - 90, 1F, 0, 0);

        GL11.glPushMatrix();
        Vec3d delta = new Vec3d(0, bullet.beamLength, 0);
        GL11.glScaled(age / 2 + 0.5, 1, age / 2 + 0.5);
        double scale = 0.075D;
        int colorInner = ((int)(0x30 * age) << 16) | ((int)(0x25 * age) << 8) | (int) (0x10 * age);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorInner, colorInner, (bullet.ticksExisted + bullet.getEntityId()) / 2, (int)(bullet.beamLength / 2 + 1), (float)scale * 4F, 2, 0.0625F);
        GL11.glPopMatrix();

        GL11.glScaled(age * 2, 1, age * 2);
        GL11.glTranslated(0, bullet.beamLength, 0);
        GL11.glRotatef(-90, 0, 0, 1);
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0xFFBF00, 0xFFFFFF, bullet.beamLength, true);

        GL11.glPopMatrix();
        RenderArcFurnace.fullbright(false);
    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_TAU_CHARGE = (bullet, interp) -> {

        RenderArcFurnace.fullbright(true);
        double age = MathHelper.clamp(1D - ((double) bullet.ticksExisted - 2 + interp) / (double) bullet.getBulletConfig().expires, 0, 1);

        GL11.glPushMatrix();
        GL11.glRotatef(180 - bullet.rotationYaw, 0, 1F, 0);
        GL11.glRotatef(-bullet.rotationPitch - 90, 1F, 0, 0);

        GL11.glPushMatrix();
        Vec3d delta = new Vec3d(0, bullet.beamLength, 0);
        GL11.glScaled(age / 2 + 0.5, 1, age / 2 + 0.5);
        double scale = 0.075D;
        int colorInner = ((int)(0x60 * age) << 16) | ((int)(0x50 * age) << 8) | (int) (0x30 * age);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorInner, colorInner, (bullet.ticksExisted + bullet.getEntityId()) / 2, (int)(bullet.beamLength / 2 + 1), (float)scale * 4F, 2, 0.0625F);
        GL11.glPopMatrix();

        GL11.glScaled(age * 2, 1, age * 2);
        GL11.glTranslated(0, bullet.beamLength, 0);
        GL11.glRotatef(-90, 0, 0, 1);
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0xFFF0A0, 0xFFFFFF, bullet.beamLength, true);

        GL11.glPopMatrix();
        RenderArcFurnace.fullbright(false);
    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_CRACKLE = (bullet, interp) -> {

        RenderArcFurnace.fullbright(true);
        double age = MathHelper.clamp(1D - ((double) bullet.ticksExisted - 2 + interp) / (double) bullet.getBulletConfig().expires, 0, 1);

        GL11.glPushMatrix();
        GL11.glRotatef(180 - bullet.rotationYaw, 0, 1F, 0);
        GL11.glRotatef(-bullet.rotationPitch - 90, 1F, 0, 0);

        double scale = 5D;
        GL11.glScaled(age * scale, 1, age * scale);
        GL11.glTranslated(0, bullet.beamLength, 0);
        GL11.glRotatef(-90, 0, 0, 1);
        renderBulletStandard(Tessellator.getInstance().getBuffer(), 0xE3D692, 0xffffff, bullet.beamLength, true);

        GL11.glPopMatrix();
        RenderArcFurnace.fullbright(false);
    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_RED = (bullet, interp) -> {
        renderStandardLaser(bullet, interp, 0x80, 0x15, 0x15);
    };
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_CYAN = (bullet, interp) -> {
        renderStandardLaser(bullet, interp, 0x15, 0x15, 0x80);
    };
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_PURPLE = (bullet, interp) -> {
        renderStandardLaser(bullet, interp, 0x60, 0x15, 0x80);
    };
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_WHITE = (bullet, interp) -> {
        renderStandardLaser(bullet, interp, 0x15, 0x15, 0x15);
    };

    public static void renderStandardLaser(EntityBulletBeamBase bullet, float interp, int r, int g, int b) {

        RenderArcFurnace.fullbright(true);
        GL11.glPushMatrix();
        GL11.glRotatef(180 - bullet.rotationYaw, 0, 1F, 0);
        GL11.glRotatef(-bullet.rotationPitch - 90, 1F, 0, 0);
        Vec3d delta = new Vec3d(0, bullet.beamLength, 0);
        double age = MathHelper.clamp(1D - ((double) bullet.ticksExisted - 2 + interp) / (double) bullet.getBulletConfig().expires, 0, 1);
        GL11.glScaled(age / 2 + 0.5, 1, age / 2 + 0.5);
        int colorInner = ((int)(r * age) << 16) | ((int)(g * age) << 8) | (int) (b * age);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorInner, colorInner, bullet.ticksExisted / 3, (int)(bullet.beamLength / 2 + 1), 0F, 8, 0.0625F);
        GL11.glPopMatrix();
        RenderArcFurnace.fullbright(false);
    }

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_FOLLY = (bullet, interp) -> {

        double age = MathHelper.clamp(1D - ((double) bullet.ticksExisted - 2 + interp) / (double) bullet.getBulletConfig().expires, 0, 1);
        RenderArcFurnace.fullbright(true);

        GL11.glPushMatrix();
        renderFlareSprite(bullet, interp, 1F, 1F, 1F, (1 - age) * 7.5 + 1.5, 0.5F * (float) age, 0.75F * (float) age);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0);
        GL11.glRotatef(180 - bullet.rotationYaw, 0, 1F, 0);
        GL11.glRotatef(-bullet.rotationPitch - 90, 1F, 0, 0);
        Vec3d delta = new Vec3d(0, bullet.beamLength, 0);
        GL11.glScaled((1 - age) * 25 + 2.5, 1, (1 - age) * 25 + 2.5);
        int colorInner = ((int)(0x20 * age) << 16) | ((int)(0x20 * age) << 8) | (int) (0x20 * age);
        BeamPronter.prontBeam(delta, BeamPronter.EnumWaveType.RANDOM, BeamPronter.EnumBeamType.SOLID, colorInner, colorInner, bullet.ticksExisted / 3, (int)(bullet.beamLength / 2 + 1), 0F, 8, 0.0625F);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        RenderArcFurnace.fullbright(false);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_NUKE = (bullet, interp) -> {

        GL11.glPushMatrix();
        GL11.glScalef(0.125F, 0.125F, 0.125F);
        GL11.glRotated(-90, 0, 1, 0);
        GL11.glTranslatef(0, -1, 1F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.fatman_mininuke_tex);
        ResourceManager.fatman.renderPart("MiniNuke");
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_HIVE = (bullet, interp) -> {

        GL11.glPushMatrix();
        GL11.glScalef(0.125F, 0.125F, 0.125F);
        GL11.glRotated(90, 0, -1, 0);
        GL11.glTranslatef(0, 0, 3.5F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.panzerschreck_tex);
        ResourceManager.panzerschreck.renderPart("Rocket");
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();
    };
}
