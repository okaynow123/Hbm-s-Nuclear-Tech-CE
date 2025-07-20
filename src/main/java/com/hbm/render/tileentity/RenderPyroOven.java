package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.oil.TileEntityMachinePyroOven;
import com.hbm.util.BobMathUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderPyroOven extends TileEntitySpecialRenderer<TileEntityMachinePyroOven> implements IItemRendererProvider {

    @Override
    public void render(TileEntityMachinePyroOven tile, double x, double y, double z, float f, int destroyStage, float alpha) {

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);

        switch(tile.getBlockMetadata() - BlockDummyable.offset) {
            case 2: GL11.glRotatef(180, 0F, 1F, 0F); break;
            case 4: GL11.glRotatef(270, 0F, 1F, 0F); break;
            case 3: GL11.glRotatef(0, 0F, 1F, 0F); break;
            case 5: GL11.glRotatef(90, 0F, 1F, 0F); break;
        }
        float anim = tile.prevAnim + (tile.anim - tile.prevAnim) * f;

        GL11.glShadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.pyrooven_tex);
        ResourceManager.pyrooven.renderPart("Oven");

        GL11.glPushMatrix();
        GL11.glTranslated(BobMathUtil.sps(anim * 0.125) / 2 - 0.5, 0, 0);
        ResourceManager.pyrooven.renderPart("Slider");
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(1.5, 0, 1.5);
        GL11.glRotated(anim * -15D % 360D, 0, 1, 0);
        GL11.glTranslated(-1.5, 0, -1.5);
        ResourceManager.pyrooven.renderPart("Fan");
        GL11.glPopMatrix();

        GL11.glShadeModel(GL11.GL_FLAT);

        GL11.glPopMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_pyrooven);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GL11.glTranslated(0, -1, 0);
                GL11.glScaled(3.5, 3.5, 3.5);
            }
            public void renderCommon() {
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glRotatef(90, 0F, 1F, 0F);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.pyrooven_tex);
                ResourceManager.pyrooven.renderAll();
                GL11.glShadeModel(GL11.GL_FLAT);
            }};
    }
}
