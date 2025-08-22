package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.storage.TileEntityFileCabinet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
@AutoRegister
public class RenderFileCabinet extends TileEntitySpecialRenderer<TileEntityFileCabinet> implements IItemRendererProvider {

    @Override
    public void render(TileEntityFileCabinet tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        switch (tile.getBlockMetadata() >> 2) { // rotation
            case 0 -> GlStateManager.rotate(180F, 0F, 1F, 0F);
            case 1 -> GlStateManager.rotate(0F, 0F, 1F, 0F);
            case 2 -> GlStateManager.rotate(270F, 0F, 1F, 0F);
            case 3 -> GlStateManager.rotate(90F, 0F, 1F, 0F);
        }

        if ((tile.getBlockMetadata() & 3) == 1) {
            this.bindTexture(ResourceManager.file_cabinet_steel_tex);
        } else {
            this.bindTexture(ResourceManager.file_cabinet_tex);
        }

        ResourceManager.file_cabinet.renderPart("Cabinet");

        GlStateManager.pushMatrix();
        float lower = tile.prevLowerExtent + (tile.lowerExtent - tile.prevLowerExtent) * partialTicks;
        GlStateManager.translate(0D, 0D, 0.6875F * lower);
        ResourceManager.file_cabinet.renderPart("LowerDrawer");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        float upper = tile.prevUpperExtent + (tile.upperExtent - tile.prevUpperExtent) * partialTicks;
        GlStateManager.translate(0D, 0D, 0.6875F * upper);
        ResourceManager.file_cabinet.renderPart("UpperDrawer");
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.filing_cabinet);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.translate(-1D, 0.5D, -1D);
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                GlStateManager.scale(4F, 4F, 4F);
            }
            public void renderCommon(ItemStack stack) {
                GlStateManager.translate(0D, -1.25D, 0D);
                GlStateManager.scale(2.75F, 2.75F, 2.75F);

                if (stack.getItemDamage() == 1) {
                    bindTexture(ResourceManager.file_cabinet_steel_tex);
                } else {
                    bindTexture(ResourceManager.file_cabinet_tex);
                }

                ResourceManager.file_cabinet.renderAll();
            }
        };
    }
}
