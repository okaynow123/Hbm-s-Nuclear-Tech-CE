package com.hbm.render.model;

import com.hbm.render.loader.ModelRendererObj;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public abstract class ModelArmorBase extends ModelBiped {

    int type;

    ModelRendererObj head;
    ModelRendererObj body;
    ModelRendererObj leftArm;
    ModelRendererObj rightArm;
    ModelRendererObj leftLeg;
    ModelRendererObj rightLeg;
    ModelRendererObj leftFoot;
    ModelRendererObj rightFoot;

    public ModelArmorBase(int type) {
        this.type = type;

        //generate null defaults to prevent major breakage from using incomplete models
        head = new ModelRendererObj(null);
        body = new ModelRendererObj(null);
        leftArm = new ModelRendererObj(null).setRotationPoint(-5.0F, 2.0F, 0.0F);
        rightArm = new ModelRendererObj(null).setRotationPoint(5.0F, 2.0F, 0.0F);
        leftLeg = new ModelRendererObj(null).setRotationPoint(1.9F, 12.0F, 0.0F);
        rightLeg = new ModelRendererObj(null).setRotationPoint(-1.9F, 12.0F, 0.0F);
        leftFoot = new ModelRendererObj(null).setRotationPoint(1.9F, 12.0F, 0.0F);
        rightFoot = new ModelRendererObj(null).setRotationPoint(-1.9F, 12.0F, 0.0F);
    }

    private static void copyModelAngles(ModelRenderer source, ModelRendererObj dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
                       float scale) {

        this.setVisible(false); //Prevents zfighting with skin layers
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        if (this.isChild) {
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
        }

        renderArmor(entityIn, scale);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float walkCycle, float walkAmplitude, float idleCycle, float headYaw, float headPitch, float scale, Entity entity) {
        if (entity instanceof EntityArmorStand entityarmorstand) {
            this.bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            this.bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            this.bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            this.isSneak = false;
        } else {
            super.setRotationAngles(walkCycle, walkAmplitude, idleCycle, headYaw, headPitch, scale, entity);
            if (entity instanceof EntityPlayer) {
                this.isSneak = entity.isSneaking();
            } else {
                this.isSneak = false;
            }

            if (entity instanceof EntityZombie) {
                boolean armsRaised = ((EntityZombie) entity).isArmsRaised();
                this.bipedLeftArm.rotateAngleY = (float) (8 * Math.PI / 180D);
                this.bipedRightArm.rotateAngleY = -(float) (8 * Math.PI / 180D);
                if (armsRaised) {
                    this.bipedLeftArm.rotateAngleX = -(float) (120 * Math.PI / 180D);
                    this.bipedRightArm.rotateAngleX = -(float) (120 * Math.PI / 180D);
                }
            }
        }

        copyModelAngles(this.bipedHead, this.head);
        copyModelAngles(this.bipedBody, this.body);
        copyModelAngles(this.bipedLeftArm, this.leftArm);
        copyModelAngles(this.bipedRightArm, this.rightArm);
        copyModelAngles(this.bipedLeftLeg, this.leftLeg);
        copyModelAngles(this.bipedRightLeg, this.rightLeg);
        copyModelAngles(this.bipedLeftLeg, this.leftFoot);
        copyModelAngles(this.bipedRightLeg, this.rightFoot);

        if (this.isSneak) {
            this.head.offsetY = 4.24F;
            this.head.rotationPointY -= 1.045F;
            this.body.offsetY = 3.45F;
            this.rightArm.offsetY = 3.45F;
            this.leftArm.offsetY = 3.45F;
            this.rightFoot.offsetZ = this.rightLeg.offsetZ = 4F;
            this.leftFoot.offsetZ = this.leftLeg.offsetZ = 4F;

            this.rightFoot.rotationPointY = 12F;
            this.rightLeg.rotationPointY = 12F;
            this.leftFoot.rotationPointY = 12F;
            this.leftLeg.rotationPointY = 12F;

            this.rightFoot.rotationPointZ = -1F;
            this.rightLeg.rotationPointZ = -1F;
            this.leftFoot.rotationPointZ = -1F;
            this.leftLeg.rotationPointZ = -1F;

        } else {
            this.head.offsetY = 0F;
            this.body.offsetY = 0F;
            this.rightArm.offsetY = 0F;
            this.leftArm.offsetY = 0F;
            this.rightFoot.offsetZ = this.rightLeg.offsetZ = 0F;
            this.leftFoot.offsetZ = this.leftLeg.offsetZ = 0F;
        }
    }

    protected abstract void renderArmor(Entity entity, float scale);
}
