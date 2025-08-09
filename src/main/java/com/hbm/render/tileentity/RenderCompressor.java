package com.hbm.render.tileentity;

import com.hbm.interfaces.AutoRegister;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineCompressor;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;

@AutoRegister
public class RenderCompressor extends TileEntitySpecialRenderer<TileEntityMachineCompressor> implements IItemRendererProvider {
    @Override
    public void render(TileEntityMachineCompressor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();

        switch(te.getBlockMetadata() - BlockDummyable.offset) {
            case 3: GlStateManager.rotate(270, 0F, 1F, 0F); break;
            case 5: GlStateManager.rotate(0, 0F, 1F, 0F); break;
            case 2: GlStateManager.rotate(90, 0F, 1F, 0F); break;
            case 4: GlStateManager.rotate(180, 0F, 1F, 0F); break;
        }

        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.compressor_tex);
        ResourceManager.compressor.renderPart("Compressor");
        
        float lift = te.prevPiston + (te.piston - te.prevPiston) * partialTicks;
        float fan = te.prevFanSpin + (te.fanSpin - te.prevFanSpin) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, lift * 3 - 3, 0);
        ResourceManager.compressor.renderPart("Pump");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 1.5, 0);
        GlStateManager.rotate(fan, 1, 0, 0);
        GlStateManager.translate(0, -1.5, 0);
        ResourceManager.compressor.renderPart("Fan");
        GlStateManager.popMatrix();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_compressor);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.translate(0, -4, 0);
                GlStateManager.scale(3, 3, 3);
            }
            public void renderCommon() {
                GlStateManager.disableCull();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);

                GlStateManager.scale(0.5, 0.5, 0.5);

                bindTexture(ResourceManager.compressor_tex);
                ResourceManager.compressor.renderPart("Compressor");

                double lift = (System.currentTimeMillis() * 0.005) % 9;

                if(lift > 3) lift = 3 - (lift - 3) / 2D;

                GlStateManager.pushMatrix();
                GlStateManager.translate(0, -lift, 0);
                ResourceManager.compressor.renderPart("Pump");
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 1.5, 0);
                GlStateManager.rotate((System.currentTimeMillis() * 0.25) % 360D, 1, 0, 0);
                GlStateManager.translate(0, -1.5, 0);
                ResourceManager.compressor.renderPart("Fan");
                GlStateManager.popMatrix();

                GlStateManager.shadeModel(GL11.GL_FLAT);
                GlStateManager.enableCull();
            }};
    }
}