package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineFan;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
@AutoRegister
public class RenderFan extends TileEntitySpecialRenderer<MachineFan.TileEntityFan> implements IItemRendererProvider {

    @Override
    public void render(MachineFan.TileEntityFan te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        GlStateManager.translate(0D, 0.5, 0D);

        switch (te.getBlockMetadata()) {
            case 0: GlStateManager.rotate(180F, 1F, 0F, 0F); break;
            case 1: break;
            case 2: GlStateManager.rotate(-90F, 1F, 0F, 0F); break;
            case 4: GlStateManager.rotate(90F, 0F, 0F, 1F); break;
            case 3: GlStateManager.rotate(90F, 1F, 0F, 0F); break;
            case 5: GlStateManager.rotate(-90F, 0F, 0F, 1F); break;
        }

        GlStateManager.translate(0D, -0.5, 0D);

        bindTexture(ResourceManager.fan_tex);
        ResourceManager.fan.renderPart("Frame");

        float rot = te.prevSpin + (te.spin - te.prevSpin) * partialTicks;
        GlStateManager.rotate(-rot, 0F, 1F, 0F);
        ResourceManager.fan.renderPart("Blades");

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.fan);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.translate(0D, -2.5D, 0D);
                double scale = 5D;
                GlStateManager.scale((float) scale, (float) scale, (float) scale);
            }
            public void renderCommon() {
                GlStateManager.scale(2F, 2F, 2F);
                bindTexture(ResourceManager.fan_tex);
                ResourceManager.fan.renderAll();
            }};
    }
}
