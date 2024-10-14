package com.hbm.particle;

import com.hbm.main.ResourceManager;
import com.hbm.util.Tuple;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ParticleSpentCasing extends Particle {

    public static final Random rand = new Random();
    private static float dScale = 0.05F, smokeJitter = 0.001F;

    private int maxSmokeGen = 120;
    private double smokeLift = 0.5D;
    private int nodeLife = 30;

    private final List<Tuple.Pair<Vec3d, Double>> smokeNodes = new ArrayList();

    private final TextureManager textureManager;

    private final SpentCasing config;
    private boolean isSmoking;

    private float momentumPitch, momentumYaw;
    private boolean onGroundPreviously = false;
    private double maxHeight;

    public float rotationPitch, rotationYaw;
    private float prevRotationPitch, prevRotationYaw;

    private boolean isInWater() {
        return this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() == Material.WATER;
    }

    public ParticleSpentCasing(TextureManager textureManager, World world, double x, double y, double z, double mx, double my, double mz, float momentumPitch, float momentumYaw, SpentCasing config) {
        super(world, x, y, z, 0, 0, 0);
        this.textureManager = textureManager;
        this.momentumPitch = momentumPitch;
        this.momentumYaw = momentumYaw;
        this.config = config;

        this.particleMaxAge = config.getMaxAge();
        this.isSmoking = rand.nextFloat() < config.getSmokeChance();
        this.maxSmokeGen = config.getSmokeDuration();
        this.smokeLift = config.getSmokeLift();
        this.nodeLife = config.getSmokeNodeLife();

        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;

        this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;

        particleGravity = 8F;

        maxHeight = y;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(motionY > 0 && posY > maxHeight)
            maxHeight = posY;

        if(!onGroundPreviously && onGround)
            tryPlayBounceSound();

        if(!onGroundPreviously && onGround) {

            onGroundPreviously = true;
            motionY = Math.log10(maxHeight - posY + 2);
            momentumPitch = (float) rand.nextGaussian() * config.getBouncePitch();
            momentumYaw = (float) rand.nextGaussian() * config.getBounceYaw();
            maxHeight = posY;

        } else if(onGroundPreviously && !onGround) {
            onGroundPreviously = false;
        }

        if(particleAge > maxSmokeGen && !smokeNodes.isEmpty())
            smokeNodes.clear();

        if(isSmoking && particleAge <= maxSmokeGen) {

            for(Tuple.Pair<Vec3d, Double> pair : smokeNodes) {
                Vec3d node = pair.getKey();

                double newX = node.x + rand.nextGaussian() * smokeJitter;
                double newZ = node.z + rand.nextGaussian() * smokeJitter;
                double newY = node.y + smokeLift * dScale;

                Vec3d updatedNode = new Vec3d(newX, newY, newZ);
                pair = new Tuple.Pair<>(updatedNode, pair.getValue());


                Double value = pair.getValue();
                value = Math.max(0, value - (1D / (double) nodeLife));
            }

            if(particleAge < maxSmokeGen || isInWater()) {
                smokeNodes.add(new Tuple.Pair<>(new Vec3d(0, 0, 0), smokeNodes.isEmpty() ? 0.0D : 1D));
            }
        }

        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;

        if(onGround) {
            rotationPitch = 0;
        } else {
            rotationPitch += momentumPitch;
            rotationYaw += momentumYaw;
        }
    }

    /** Used for frame-perfect translation of smoke */
    private boolean setupDeltas = false;
    private double prevRenderX;
    private double prevRenderY;
    private double prevRenderZ;

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float interp, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

        GL11.glPushMatrix();
        RenderHelper.enableStandardItemLighting();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDepthMask(true);

        double pX = prevPosX + (posX - prevPosX) * interp;
        double pY = prevPosY + (posY - prevPosY) * interp;
        double pZ = prevPosZ + (posZ - prevPosZ) * interp;

        if(!setupDeltas) {
            prevRenderX = pX;
            prevRenderY = pY;
            prevRenderZ = pZ;
            setupDeltas = true;
        }
        BlockPos blockPos = new BlockPos(pX, pY, pZ);
        int brightness = world.getCombinedLight(blockPos, 0);
        int lX = brightness % 65536;
        int lY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lX / 1.0F, (float)lY / 1.0F);

        textureManager.bindTexture(ResourceManager.casings_tex);

        EntityPlayer player = Minecraft.getMinecraft().player;
        double dX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)interp;
        double dY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)interp;
        double dZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)interp;

        GL11.glTranslated(pX - dX, pY - dY - this.height / 4 + config.getScaleY() * 0.01, pZ - dZ);

        GL11.glScalef(dScale, dScale, dScale);

        GL11.glRotatef(180 - momentumYaw, 0, 1, 0);
        GL11.glRotatef(-momentumPitch, 1, 0, 0);

        GL11.glScalef(config.getScaleX(), config.getScaleY(), config.getScaleZ());

        int index = 0;
        for(String name : config.getType().partNames) {
            int col = this.config.getColors()[index]; //unsafe on purpose, set your colors properly or else...!
            Color color = new Color(col);
            GL11.glColor3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
            ResourceManager.casings.renderPart(name);
            index++;
        }

        GL11.glColor3f(1F, 1F, 1F);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(pX - dX, pY - dY - this.height / 4, pZ - dZ);
        //GL11.glScalef(dScale, dScale, dScale);
        //GL11.glScalef(config.getScaleX(), config.getScaleY(), config.getScaleZ());

        if(!smokeNodes.isEmpty()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            float scale = config.getScaleX() * 0.5F * dScale;
            Vec3d vec = new Vec3d(scale, 0, 0);
            float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * interp;
            vec.rotateYaw((float) Math.toRadians(-yaw));

            double deltaX = prevRenderX - pX;
            double deltaY = prevRenderY - pY;
            double deltaZ = prevRenderZ - pZ;

            for(Tuple.Pair<Vec3d, Double> pair : smokeNodes) {
                Vec3d pos = pair.getKey();
                double mult = 1D;
                double newX = pos.x + deltaX * mult;
                double newY = pos.y + deltaY * mult;
                double newZ = pos.z + deltaZ * mult;
                Vec3d newPos = new Vec3d(newX, newY, newZ);

                pair = new Tuple.Pair<>(newPos, pair.getValue());

            }

            for(int i = 0; i < smokeNodes.size() - 1; i++) {
                final Tuple.Pair<Vec3d, Double> node = smokeNodes.get(i), past = smokeNodes.get(i + 1);
                final Vec3d nodeLoc = node.getKey(), pastLoc = past.getKey();
                float nodeAlpha = node.getValue().floatValue();
                float pastAlpha = past.getValue().floatValue();

                double timeAlpha = 1D - (double) particleAge / (double) maxSmokeGen;
                nodeAlpha *= timeAlpha;
                pastAlpha *= timeAlpha;

                bufferbuilder.pos(nodeLoc.x, nodeLoc.y, nodeLoc.z).color(1F, 1F, 1F, nodeAlpha).endVertex();
                bufferbuilder.pos(nodeLoc.x + vec.x, nodeLoc.y, nodeLoc.z + vec.z).color(1F, 1F, 1F, 0F).endVertex();
                bufferbuilder.pos(pastLoc.x + vec.x, pastLoc.y, pastLoc.z + vec.z).color(1F, 1F, 1F, 0F).endVertex();
                bufferbuilder.pos(pastLoc.x, pastLoc.y, pastLoc.z).color(1F, 1F, 1F, pastAlpha).endVertex();

                bufferbuilder.pos(nodeLoc.x, nodeLoc.y, nodeLoc.z).color(1F, 1F, 1F, nodeAlpha).endVertex();
                bufferbuilder.pos(nodeLoc.x - vec.x, nodeLoc.y, nodeLoc.z - vec.z).color(1F, 1F, 1F, 0F).endVertex();
                bufferbuilder.pos(pastLoc.x - vec.x, pastLoc.y, pastLoc.z - vec.z).color(1F, 1F, 1F, 0F).endVertex();
                bufferbuilder.pos(pastLoc.x, pastLoc.y, pastLoc.z).color(1F, 1F, 1F, pastAlpha).endVertex();
            }

            GL11.glAlphaFunc(GL11.GL_GREATER, 0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_CULL_FACE);
            tessellator.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1F);
        }

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();

        RenderHelper.disableStandardItemLighting();

        prevRenderX = pX;
        prevRenderY = pY;
        prevRenderZ = pZ;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));

        if (this.world.isBlockLoaded(blockpos)) {
            double d0 = (this.getBoundingBox().maxY - this.getBoundingBox().minY) * 0.66D;
            int k = MathHelper.floor(this.posY + d0);
            blockpos.setY(k);
            return this.world.getCombinedLight(blockpos, 0);
        } else {
            return 0;
        }
    }

    private void tryPlayBounceSound() {

        SoundEvent sound = config.getSound();

        if(sound != null) {
            world.playSound(null, this.posX, this.posY, this.posZ, sound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        }
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }
}
