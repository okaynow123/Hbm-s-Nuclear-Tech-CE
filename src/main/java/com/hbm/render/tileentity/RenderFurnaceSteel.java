package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityFurnaceSteel;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderFurnaceSteel extends TileEntitySpecialRenderer<TileEntityFurnaceSteel>
        implements IItemRendererProvider {

    @Override
    public void render(
            TileEntityFurnaceSteel tileEntity,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y, z + 0.5D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);

        switch (tileEntity.getBlockMetadata() - BlockDummyable.offset) {
            case 3:
                GL11.glRotatef(0, 0F, 1F, 0F);
                break;
            case 5:
                GL11.glRotatef(90, 0F, 1F, 0F);
                break;
            case 2:
                GL11.glRotatef(180, 0F, 1F, 0F);
                break;
            case 4:
                GL11.glRotatef(270, 0F, 1F, 0F);
                break;
        }

        GL11.glRotatef(-90, 0F, 1F, 0F);

        bindTexture(ResourceManager.furnace_steel_tex);
        ResourceManager.furnace_steel.renderAll();
        if (tileEntity.wasOn) {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            float col = (float) Math.sin(System.currentTimeMillis() * 0.001);
            float r = 0.875F + col * 0.125F;
            float g = 0.625F + col * 0.375F;
            float b = 0F;
            float a = 0.5F;

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

            int light = 240; // max brightness
            for (int i = 0; i < 4; i++) {
                double xOffset = 1 + i * 0.0625;

                buffer.pos(xOffset, 1, -1)
                        .color(r, g, b, a)
                        .lightmap(light, light)
                        .endVertex();
                buffer.pos(xOffset, 1, 1)
                        .color(r, g, b, a)
                        .lightmap(light, light)
                        .endVertex();
                buffer.pos(xOffset, 0.5, 1)
                        .color(r, g, b, a)
                        .lightmap(light, light)
                        .endVertex();
                buffer.pos(xOffset, 0.5, -1)
                        .color(r, g, b, a)
                        .lightmap(light, light)
                        .endVertex();
            }

            tess.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }
        GL11.glPopMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.furnace_steel);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.translate(0, -1.5, 0);
                GlStateManager.scale(3.25, 3.25, 3.25);
            }

            public void renderCommon() {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.furnace_steel_tex);
                ResourceManager.furnace_steel.renderAll();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }
        };
    }
}
