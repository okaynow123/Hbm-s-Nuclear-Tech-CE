package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.albion.TileEntityPARFC;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderPARFC extends TileEntitySpecialRenderer<TileEntityPARFC> implements IItemRendererProvider {

    @Override
    public void render(@NotNull TileEntityPARFC tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y - 1.0D, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        switch (tile.getBlockMetadata() - BlockDummyable.offset) {
            case 4 -> GlStateManager.rotate(180, 0F, 1F, 0F);
            case 3 -> GlStateManager.rotate(270, 0F, 1F, 0F);
            case 5 -> GlStateManager.rotate(0, 0F, 1F, 0F);
            case 2 -> GlStateManager.rotate(90, 0F, 1F, 0F);
        }

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.pa_rfc_tex);
        ResourceManager.pa_rfc.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.pa_rfc);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            @Override
            public void renderInventory() {
                GlStateManager.translate(0, -1, 0);
                double scale = 4;
                GlStateManager.scale(scale, scale, scale);
            }

            @Override
            public void renderCommon() {
                double scale = 0.5;
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.pa_rfc_tex);
                ResourceManager.pa_rfc.renderAll();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }
        };
    }
}