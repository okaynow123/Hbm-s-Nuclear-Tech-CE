package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import com.hbm.util.BobMathUtil;
import com.hbm.util.Vec3NT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ItemRenderSexy extends ItemRenderWeaponBase {

    protected ResourceLocation texture;

    public ItemRenderSexy(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
        return  fov * (1 - aimingProgress * 0.33F);
    }

    @Override
    public void setupFirstPerson(ItemStack stack) {
        GlStateManager.translate(0, 0, 0.875);

        float offset = 0.8F;

        standardAimingTransform(stack,
                -1F * offset, -0.75F * offset, 3F * offset,
                -0.5F, -0.5F, 2F);
    }

    @Override
    public void renderFirstPerson(ItemStack stack) {
        ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
        double scale = 0.375D;
        GlStateManager.scale((float) scale, (float) scale, (float) scale);

        boolean doesCycle = HbmAnimationsSedna.getRelevantAnim(0) != null && HbmAnimationsSedna.getRelevantAnim(0).animation.getBus("CYCLE") != null;
        boolean reloading = HbmAnimationsSedna.getRelevantAnim(0) != null && HbmAnimationsSedna.getRelevantAnim(0).animation.getBus("BELT") != null;
        boolean useShellCount = HbmAnimationsSedna.getRelevantAnim(0) != null && HbmAnimationsSedna.getRelevantAnim(0).animation.getBus("SHELLS") != null;
        boolean girldinner = HbmAnimationsSedna.getRelevantAnim(0) != null && HbmAnimationsSedna.getRelevantAnim(0).animation.getBus("BOTTLE") != null;
        double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
        double[] lower = HbmAnimationsSedna.getRelevantTransformation("LOWER");
        double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
        double[] cycle = HbmAnimationsSedna.getRelevantTransformation("CYCLE");
        double[] barrel = HbmAnimationsSedna.getRelevantTransformation("BARREL");
        double[] hood = HbmAnimationsSedna.getRelevantTransformation("HOOD");
        double[] lever = HbmAnimationsSedna.getRelevantTransformation("LEVER");
        double[] belt = HbmAnimationsSedna.getRelevantTransformation("BELT");
        double[] mag = HbmAnimationsSedna.getRelevantTransformation("MAG");
        double[] magRot = HbmAnimationsSedna.getRelevantTransformation("MAGROT");
        double[] shellCount = HbmAnimationsSedna.getRelevantTransformation("SHELLS");
        double[] bottle = HbmAnimationsSedna.getRelevantTransformation("BOTTLE");
        double[] sippy = HbmAnimationsSedna.getRelevantTransformation("SIP");

        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        if (girldinner) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) bottle[0], (float) bottle[1], (float) bottle[2]);
            GlStateManager.translate(0.0F, 2.0F, 0.0F);
            GlStateManager.rotate((float) sippy[0], 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -2.0F, 0.0F);
            GlStateManager.scale(1.5F, 1.5F, 1.5F);
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.whiskey_tex);
            ResourceManager.whiskey.renderAll();
            GlStateManager.popMatrix();
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        GlStateManager.translate(0.0F, -1.0F, -8.0F);
        GlStateManager.rotate((float) equip[0], 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 1.0F, 8.0F);

        GlStateManager.translate(0.0F, 0.0F, -6.0F);
        GlStateManager.rotate((float) lower[0], 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 6.0F);

        GlStateManager.translate(0.0F, 0.0F, (float) recoil[2]);

        ResourceManager.sexy.renderPart("Gun");

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, (float) barrel[2]);
        ResourceManager.sexy.renderPart("Barrel");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, -0.375F);
        GlStateManager.scale(1.0F, 1.0F, (float) (1.0D + 0.457247371D * barrel[2]));
        GlStateManager.translate(0.0F, 0.0F, 0.375F);
        ResourceManager.sexy.renderPart("RecoilSpring");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.4375F, -2.875F);
        GlStateManager.rotate((float) hood[0], 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.4375F, 2.875F);
        ResourceManager.sexy.renderPart("Hood");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.46875F, -6.875F);
        GlStateManager.rotate((float) (lever[2] * 60.0D), 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.46875F, 6.875F);
        ResourceManager.sexy.renderPart("Lever");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, -6.75F);
        GlStateManager.scale(1.0F, 1.0F, (float) (1.0D - lever[2] * 0.25D));
        GlStateManager.translate(0.0F, 0.0F, 6.75F);
        ResourceManager.sexy.renderPart("LockSpring");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) mag[0], (float) mag[1], (float) mag[2]);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
        GlStateManager.rotate((float) magRot[2], 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        ResourceManager.sexy.renderPart("Magazine");

        double p = 0.0625D;
        double x = p * 17;
        double y = p * -26;
        double angle = 0;
        Vec3NT vec = new Vec3NT(0, 0.4375, 0);

        double[] anglesLoaded = new double[]   {0,   0,  20,  20,  50, 60, 70};
        double[] anglesUnloaded = new double[] {0, -10, -50, -60, -60,  0,  0};
        double reloadProgress = !reloading ? 1D : belt[0];
        double cycleProgress = !doesCycle ? 1 : cycle[0];

        double[][] shells = new double[anglesLoaded.length][3];

        for (int i = 0; i < anglesLoaded.length; i++) {
            shells[i][0] = x;
            shells[i][1] = y;
            shells[i][2] = angle - 90;
            double delta = BobMathUtil.interp(anglesUnloaded[i], anglesLoaded[i], reloadProgress);
            angle += delta;
            vec.rotateAroundZDeg(-delta);
            x += vec.x;
            y += vec.y;
        }

        int shellAmount = useShellCount ? (int) shellCount[0] : gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null);

        for (int i = 0; i < shells.length - 1; i++) {
            double[] prevShell = shells[i];
            double[] nextShell = shells[i + 1];
            renderShell(prevShell[0], nextShell[0], prevShell[1], nextShell[1], prevShell[2], nextShell[2], shells.length - i < shellAmount + 2, cycleProgress);
        }
        GlStateManager.popMatrix();

        GlStateManager.shadeModel(GL11.GL_FLAT);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 8.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F * gun.shotRand, 1.0F, 0.0F, 0.0F);
        this.renderMuzzleFlash(gun.lastShot[0], 150, 7.5);
        GlStateManager.popMatrix();
    }

    @Override
    public void setupThirdPerson(ItemStack stack) {
        super.setupThirdPerson(stack);
        double scale = 1.75D;
        GlStateManager.scale((float) scale, (float) scale, (float) scale);
        GlStateManager.translate(1.0F, 1.0F, 6.0F);
    }

    @Override
    public void setupInv(ItemStack stack) {
        super.setupInv(stack);
        double scale = 1.375D;
        GlStateManager.scale((float) scale, (float) scale, (float) scale);
        GlStateManager.rotate(25.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.5F, 0.25F);
    }

    @Override
    public void setupModTable(ItemStack stack) {
        double scale = -9.5D;
        GlStateManager.scale((float) scale, (float) scale, (float) scale);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void renderOther(ItemStack stack, Object type) {
        GlStateManager.enableLighting();

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        ResourceManager.sexy.renderPart("Gun");
        ResourceManager.sexy.renderPart("Barrel");
        ResourceManager.sexy.renderPart("RecoilSpring");
        ResourceManager.sexy.renderPart("Hood");
        ResourceManager.sexy.renderPart("Lever");
        ResourceManager.sexy.renderPart("LockSpring");
        ResourceManager.sexy.renderPart("Magazine");

        double p = 0.0625D;
        renderShell(p *  0, p *  -6,  90, true);
        renderShell(p *  5, p *   1,  30, true);
        renderShell(p * 12, p *  -1, -30, true);
        renderShell(p * 17, p *  -6, -60, true);
        renderShell(p * 17, p * -13, -90, true);
        renderShell(p * 17, p * -20, -90, true);

        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    public static void renderShell(double x0, double x1, double y0, double y1, double rot0, double rot1, boolean shell, double interp) {
        renderShell(BobMathUtil.interp(x0, x1, interp), BobMathUtil.interp(y0, y1, interp), BobMathUtil.interp(rot0, rot1, interp), shell);
    }

    public static void renderShell(double x, double y, double rot, boolean shell) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, 0.375 + y, 0);
        GlStateManager.rotate(rot, 0, 0, 1);
        GlStateManager.translate(0, -0.375, 0);
        ResourceManager.sexy.renderPart("Belt");
        if(shell) ResourceManager.sexy.renderPart("Shell");
        GlStateManager.popMatrix();
    }
}
