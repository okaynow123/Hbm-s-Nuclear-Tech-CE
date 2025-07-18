package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineAmmoPress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

public class RenderAmmoPress extends TileEntitySpecialRenderer<TileEntityMachineAmmoPress>
    implements IItemRendererProvider {

  @Override
  public void render(
      TileEntityMachineAmmoPress ammo_press,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    switch (ammo_press.getBlockMetadata() - BlockDummyable.offset) {
      case 2:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
      case 4:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 3:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 5:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
    }

    float press = ammo_press.prevPress + (ammo_press.press - ammo_press.prevPress) * partialTicks;
    float lift = ammo_press.prevLift + (ammo_press.lift - ammo_press.prevLift) * partialTicks;

    bindTexture(ResourceManager.ammo_press_tex);
    ResourceManager.ammo_press.renderPart("Frame");

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, -press * 0.25F, 0);
    ResourceManager.ammo_press.renderPart("Press");
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, lift * 0.5F - 0.5F, 0);
    ResourceManager.ammo_press.renderPart("Shells");
    if (ammo_press.animState == TileEntityMachineAmmoPress.AnimationState.RETRACTING
        || ammo_press.animState == TileEntityMachineAmmoPress.AnimationState.LOWERING)
      ResourceManager.ammo_press.renderPart("Bullets");
    GlStateManager.popMatrix();

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_ammo_press);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -2.5, 0);
        GlStateManager.scale(5, 5, 5);
      }

      public void renderCommon() {
        GlStateManager.rotate(90, 0F, 1F, 0F);
        bindTexture(ResourceManager.ammo_press_tex);
        ResourceManager.ammo_press.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
