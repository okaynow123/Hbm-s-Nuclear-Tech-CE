package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineAssemfac;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderAssemfac extends TileEntitySpecialRenderer<TileEntityMachineAssemfac>
        implements IItemRendererProvider {

    @Override
    public boolean isGlobalRenderer(TileEntityMachineAssemfac te) {
        return true;
    }

    @Override
    public void render(
            TileEntityMachineAssemfac tileEntity,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.disableCull();

        switch (tileEntity.getBlockMetadata() - BlockDummyable.offset) {
            case 5:
                GlStateManager.rotate(180.0F, 0F, 1F, 0F);
                break;
            case 2:
                GlStateManager.rotate(270.0F, 0F, 1F, 0F);
                break;
            case 4:
                GlStateManager.rotate(0.0F, 0F, 1F, 0F);
                break;
            case 3:
                GlStateManager.rotate(90.0F, 0F, 1F, 0F);
                break;
        }

        GlStateManager.translate(0.5D, 0.0D, -0.5D);

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.assemfac_tex);
        ResourceManager.assemfac.renderPart("Factory");

        double hOff;
        double sOff;

        for (int i = 0; i < tileEntity.arms.length; i++) {

            TileEntityMachineAssemfac.AssemblerArm arm = tileEntity.arms[i];
            double pivotRot = arm.prevAngles[0] + (arm.angles[0] - arm.prevAngles[0]) * partialTicks;
            double armRot = arm.prevAngles[1] + (arm.angles[1] - arm.prevAngles[1]) * partialTicks;
            double pistonRot = arm.prevAngles[2] + (arm.angles[2] - arm.prevAngles[2]) * partialTicks;
            double striker = arm.prevAngles[3] + (arm.angles[3] - arm.prevAngles[3]) * partialTicks;

            int side = i < 3 ? 1 : -1;
            int index = i + 1;

            GlStateManager.pushMatrix();

            hOff = 1.875D;
            sOff = 2D * side;
            GlStateManager.translate(sOff, hOff, sOff);
            GlStateManager.rotate((float) (pivotRot * side), 1F, 0F, 0F);
            GlStateManager.translate(-sOff, -hOff, -sOff);
            ResourceManager.assemfac.renderPart("Pivot" + index);

            hOff = 3.375D;
            sOff = 2D * side;
            GlStateManager.translate(sOff, hOff, sOff);
            GlStateManager.rotate((float) (armRot * side), 1F, 0F, 0F);
            GlStateManager.translate(-sOff, -hOff, -sOff);
            ResourceManager.assemfac.renderPart("Arm" + index);

            hOff = 3.375D;
            sOff = 0.625D * side;
            GlStateManager.translate(sOff, hOff, sOff);
            GlStateManager.rotate((float) (pistonRot * side), 1F, 0F, 0F);
            GlStateManager.translate(-sOff, -hOff, -sOff);
            ResourceManager.assemfac.renderPart("Piston" + index);
            GlStateManager.translate(0, -striker, 0);
            ResourceManager.assemfac.renderPart("Striker" + index);

            GlStateManager.popMatrix();
        }

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();

    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_assemfac);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.scale(2.5, 2.5, 2.5);
            }

            public void renderCommon() {
                GlStateManager.scale(0.5, 0.5, 0.5);
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.assemfac_tex);
                ResourceManager.assemfac.renderPart("Factory");
                for (int i = 1; i < 7; i++) {
                    ResourceManager.assemfac.renderPart("Pivot" + i);
                    ResourceManager.assemfac.renderPart("Arm" + i);
                    ResourceManager.assemfac.renderPart("Piston" + i);
                    ResourceManager.assemfac.renderPart("Striker" + i);
                }
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }
        };
    }
}
