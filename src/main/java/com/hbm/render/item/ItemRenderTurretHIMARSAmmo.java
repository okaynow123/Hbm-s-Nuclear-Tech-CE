package com.hbm.render.item;

import com.hbm.interfaces.AutoRegister;
import com.hbm.items.weapon.ItemAmmoHIMARS;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static com.hbm.render.NTMRenderHelper.bindTexture;
@AutoRegister(item = "ammo_himars")
public class ItemRenderTurretHIMARSAmmo extends ItemRenderBase {
  @Override
  public void renderInventory() {
    GlStateManager.translate(0, -2, 0);
    GlStateManager.scale(2.75, 2.75, 2.75);
    GlStateManager.rotate((float) (System.currentTimeMillis() % 3600) / 10, 0, 1, 0);
  }

  @Override
  public void renderCommon(ItemStack item) {
    GlStateManager.translate(0, 1.5, 0);
    GlStateManager.rotate(-45, 0, 1, 0);
    GlStateManager.rotate(90, 1, 0, 0);
    ItemAmmoHIMARS.HIMARSRocket type = ItemAmmoHIMARS.itemTypes[item.getItemDamage()];
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(type.texture);
    if (type.modelType == ItemAmmoHIMARS.HIMARSRocket.Type.Standard) {
      GlStateManager.translate(0.75, 0, 0);
      ResourceManager.turret_himars.renderPart("RocketStandard");
      GlStateManager.translate(-1.5, 0, 0);
      GlStateManager.translate(0, -3.375D, 0);
      ResourceManager.turret_himars.renderPart("TubeStandard");
    } else {
      GlStateManager.translate(0.75, 0, 0);
      ResourceManager.turret_himars.renderPart("RocketSingle");
      GlStateManager.translate(-1.5, 0, 0);
      GlStateManager.translate(0, -3.375D, 0);
      ResourceManager.turret_himars.renderPart("TubeSingle");
    }
    GlStateManager.shadeModel(GL11.GL_FLAT);
  }
}
