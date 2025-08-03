package com.hbm.render.tileentity;

import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.tileentity.machine.TileEntityZirnoxDestroyed;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;
@AutoRegister
public class RenderZirnoxDestroyed extends TileEntitySpecialRenderer<TileEntityZirnoxDestroyed> {

    @Override
    public void render(TileEntityZirnoxDestroyed tileEntity, double x, double y, double z, float interp, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.disableCull();
        switch(tileEntity.getBlockMetadata() - 10) {
            case 2:
                GlStateManager.rotate(90, 0F, 1F, 0F); break;
            case 3:
                GlStateManager.rotate(270, 0F, 1F, 0F); break;
            case 4:
                GlStateManager.rotate(180, 0F, 1F, 0F); break;
            case 5:
                GlStateManager.rotate(0, 0F, 1F, 0F); break;
        }

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.zirnox_destroyed_tex);
        ResourceManager.zirnox_destroyed.renderAll();

        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);

        GlStateManager.popMatrix();

    }
}
