package com.hbm.render.item;

import com.hbm.blocks.machine.MachineCapacitor;
import com.hbm.hfr.render.loader.WavefrontObjVBO;
import com.hbm.main.ResourceManager;
import com.hbm.render.tileentity.RenderCapacitor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ItemRenderCapacitor extends ItemRenderBase {

    ItemRenderCapacitor(Block block) {
        if(block instanceof MachineCapacitor) capacitor = (MachineCapacitor) block; // just in case someone's stupid enough to use it NOT for capacitors..
    }

    private MachineCapacitor capacitor;

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        GL11.glPushMatrix();
        GlStateManager.enableCull();
        switch(type){
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                GL11.glScaled(1.0, 1.0, 1.0);
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                GL11.glRotated(-90, 0, 1, 0);
                GL11.glScaled(1.0, 1.0, 1.0);
                break;
            case HEAD:
            case FIXED:
            case GROUND:
                GL11.glScaled(0.4, 0.4, 0.4);
                GL11.glRotated(-90, 0, 1, 0);
                GlStateManager.translate(2.5, 0, 0);
                renderNonInv(itemStackIn);
                break;
            case GUI:
                GlStateManager.enableLighting();
                GL11.glRotated(30, 1, 0, 0);
                GL11.glRotated(45+180, 0, 1, 0);
                GL11.glScaled(0.062, 0.062, 0.062);
                GL11.glTranslated(0, 12, -11.3);
                renderInventory(itemStackIn);
                break;
            case NONE:
                break;
        }
        renderCommon(itemStackIn);
        GL11.glPopMatrix();
    }

    public void renderNonInv(ItemStack stack) { renderNonInv(); }
    public void renderInventory(ItemStack stack) { GL11.glScaled(11.0, 11.0, 11.0); }
    public void renderCommon(ItemStack stack) { renderCommon(); }
    public void renderNonInv() {
        GL11.glScaled(1.0, 1.0, 1.0);
        GL11.glRotated(0, 0, 1, 0);
    }
    public void renderCommon() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        /*RenderCapacitor.renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "Top", capacitor.iconTop);
        RenderCapacitor.renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "Side", capacitor.iconSide);
        RenderCapacitor.renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "Bottom", capacitor.iconBottom);
        RenderCapacitor.renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "InnerTop", capacitor.iconInnerTop);
        RenderCapacitor.renderPartWithIcon((WavefrontObjVBO) ResourceManager.capacitor, "InnerSide", capacitor.iconInnerSide);*/

        GlStateManager.shadeModel(GL11.GL_FLAT);
    }
}
