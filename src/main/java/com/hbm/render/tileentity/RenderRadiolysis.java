package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineRadiolysis;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

@AutoRegister
public class RenderRadiolysis extends TileEntitySpecialRenderer<TileEntityMachineRadiolysis> implements IItemRendererProvider{
    @Override
    public void render(TileEntityMachineRadiolysis tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.disableCull();

        switch (tileEntity.getBlockMetadata() - BlockDummyable.offset) {
            case 2 -> GlStateManager.rotate(180, 0F, 1F, 0F);
            case 3 -> GlStateManager.rotate(0, 0F, 1F, 0F);
            case 4 -> GlStateManager.rotate(90, 0F, 1F, 0F);
            case 5 -> GlStateManager.rotate(270, 0F, 1F, 0F);
        }

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.radiolysis_tex);
        ResourceManager.radiolysis.renderAll();

        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_radiolysis);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase( ) {
            @Override
            public void renderInventory() {
                GlStateManager.translate(0, -2.5, 0);
                GlStateManager.scale(3, 3, 3);
            }

            @Override
            public void renderCommon() {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.radiolysis_tex); ResourceManager.radiolysis.renderAll();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            @Override
            public void renderNonInv() {
                GlStateManager.rotate(-45, 0, 1, 0);
                GlStateManager.translate(-0.5F, -0.5F, 0);
            }
        };
    }
}
