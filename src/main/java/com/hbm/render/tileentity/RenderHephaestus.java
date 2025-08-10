package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineHephaestus;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

@AutoRegister
public class RenderHephaestus extends TileEntitySpecialRenderer<TileEntityMachineHephaestus> implements IItemRendererProvider {

    @Override
    public void render(TileEntityMachineHephaestus tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.enableLighting();
        GlStateManager.disableCull();

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.hephaestus_tex);
        ResourceManager.hephaestus.renderPart("Main");

        float movement = tile.prevRot + (tile.rot - tile.prevRot) * partialTicks;
        boolean isOn = tile.bufferedHeat > 0;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(movement, 0, 1, 0);

        for(int i = 0; i < 3; i++) {
            ResourceManager.hephaestus.renderPart("Rotor");
            GlStateManager.rotate(120, 0, 1, 0);
        }
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();


        GlStateManager.pushMatrix();

        if(isOn) {
            bindTexture(RenderCrucible.lava);
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        } else {
            bindTexture(RenderExcavator.cobble);
            GlStateManager.color(0.5F, 0.5F, 0.5F);
        }

        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(0, movement / 10F, 0);
        ResourceManager.hephaestus.renderPart("Core");
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        if(isOn) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_hephaestus);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.translate(0, -4.5, 0);
                GlStateManager.scale(2.25, 2.25, 2.25);
            }
            public void renderCommon() {
                GlStateManager.scale(0.5, 0.5, 0.5);
                GlStateManager.disableCull();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.hephaestus_tex);
                ResourceManager.hephaestus.renderPart("Main");

                GlStateManager.pushMatrix();

                for(int i = 0; i < 3; i++) {
                    ResourceManager.hephaestus.renderPart("Rotor");
                    GlStateManager.rotate(120, 0, 1, 0);
                }
                GlStateManager.enableCull();
                GlStateManager.shadeModel(GL11.GL_FLAT);
                GlStateManager.popMatrix();
                bindTexture(RenderExcavator.cobble);
                ResourceManager.hephaestus.renderPart("Core");
            }};
    }
}
