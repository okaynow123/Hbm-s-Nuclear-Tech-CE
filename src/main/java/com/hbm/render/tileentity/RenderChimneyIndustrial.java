package com.hbm.render.tileentity;

import com.hbm.main.ResourceManager;
import com.hbm.tileentity.machine.TileEntityChimneyIndustrial;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

public class RenderChimneyIndustrial extends TileEntitySpecialRenderer<TileEntityChimneyIndustrial> {

    @Override
    public boolean isGlobalRenderer(TileEntityChimneyIndustrial te) {
        return true;
    }

    @Override
    public void render(TileEntityChimneyIndustrial tileEntity, double x, double y, double z, float f, int destroyStage, float alpha) {

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y, z + 0.5D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glRotatef(180, 0F, 1F, 0F);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.chimney_industrial_tex);
        ResourceManager.chimney_industrial.renderAll();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glPopMatrix();
    }
}
