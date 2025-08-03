package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.albion.TileEntityPADipole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
@AutoRegister
public class RenderPADipole extends TileEntitySpecialRenderer<TileEntityPADipole> implements IItemRendererProvider {

    @Override
    public void render(@NotNull TileEntityPADipole tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y - 1.0D, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.pa_dipole_tex);
        ResourceManager.pa_dipole.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.pa_dipole);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            @Override
            public void renderInventory() {
                GlStateManager.translate(0, -3, 0);
                double scale = 3.5;
                GlStateManager.scale(scale, scale, scale);
            }

            @Override
            public void renderCommon() {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.pa_dipole_tex);
                ResourceManager.pa_dipole.renderAll();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }
        };
    }
}